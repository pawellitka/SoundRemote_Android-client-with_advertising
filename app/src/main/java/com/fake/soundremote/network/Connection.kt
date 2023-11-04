package com.fake.soundremote.network

import android.os.Build
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.Net
import com.fake.soundremote.util.SystemMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketException
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.AlreadyBoundException
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.DatagramChannel
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal class Connection(
    private val audioDataSink: SendChannel<ByteArray>,
    private val connectionMessages: SendChannel<SystemMessage>
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var receiveJob: Job? = null
    private var keepAliveJob: Job? = null
    private var pendingRequests = mutableMapOf<Net.PacketType, Request>()

    private var serverAddress: InetSocketAddress? = null
    private var dataChannel: DatagramChannel? = null
    private var sendChannel: DatagramChannel? = null
    private val sendLock = Any()

    private var serverProtocol: UByte = 1u
    private var serverLastContact: Long = 0
    private var _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus>
        get() = _connectionStatus
    private var currentStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
    var processAudio: Boolean = true
    private fun updateStatus(newStatus: ConnectionStatus) {
        currentStatus = newStatus
        _connectionStatus.value = newStatus
    }

    suspend fun connect(
        address: String,
        serverPort: Int,
        localPort: Int,
        @Net.Compression compression: Int
    ): Boolean =
        withContext(Dispatchers.IO) {
            shutdown()

            updateStatus(ConnectionStatus.CONNECTING)
            synchronized(sendLock) {
                serverAddress = InetSocketAddress(address, serverPort)
                sendChannel = DatagramChannel.open()
            }
            dataChannel = DatagramChannel.open().also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.setOption(StandardSocketOptions.SO_REUSEADDR, true)
                }
            }

            val bindAddress = InetSocketAddress(localPort)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    dataChannel?.bind(bindAddress)
                } catch (e: AlreadyBoundException) {
                    sendMessage(SystemMessage.MESSAGE_ALREADY_BOUND)
                    shutdown()
                    return@withContext false
                } catch (e: IOException) {
                    sendMessage(SystemMessage.MESSAGE_BIND_ERROR)
                    shutdown()
                    return@withContext false
                }
            } else {
                try {
                    dataChannel?.socket()?.bind(bindAddress)
                } catch (e: SocketException) {
                    sendMessage(SystemMessage.MESSAGE_BIND_ERROR)
                    shutdown()
                    return@withContext false
                }
            }
            receiveJob = receive()
            keepAliveJob = keepAlive()
            sendConnect(compression)
            true
        }

    /**
     * Sends the disconnect packet and closes the connection.
     */
    fun disconnect() {
        scope.launch {
            send(Net.getDisconnectPacket())
            shutdown()
        }
    }

    private suspend fun shutdown() = withContext(Dispatchers.IO) {
        if (currentStatus == ConnectionStatus.DISCONNECTED) return@withContext
        synchronized(sendLock) {
            serverAddress = null
            sendChannel?.close()
            sendChannel = null
        }

        receiveJob?.cancel()
        keepAliveJob?.cancel()

        // Close channel after cancelling receiving job to avoid trying to invoke receive
        // from closed or null channel
        dataChannel?.close()
        dataChannel = null

        updateStatus(ConnectionStatus.DISCONNECTED)
    }

    private fun sendConnect(@Net.Compression compression: Int) {
        val request = Request()
        val packet = Net.getConnectPacket(compression, request.id)
        scope.launch { send(packet) }
        pendingRequests[Net.PacketType.CONNECT] = request
    }

    fun sendSetFormat(@Net.Compression compression: Int) {
        val request = Request()
        val packet = Net.getSetFormatPacket(compression, request.id)
        scope.launch { send(packet) }
        pendingRequests[Net.PacketType.SET_FORMAT] = request
    }

    fun sendKeystroke(keyCode: Int, mods: Int) {
        val keystrokePacket = Net.getKeystrokePacket(keyCode, mods)
        scope.launch { send(keystrokePacket) }
    }

    private suspend fun send(data: ByteBuffer) = withContext(Dispatchers.IO) {
        synchronized(sendLock) {
            serverAddress?.let { address ->
                sendChannel?.send(data, address)
            }
        }
    }

    private suspend fun sendMessage(message: SystemMessage) {
        connectionMessages.send(message)
    }

    private fun receive() = scope.launch(Dispatchers.IO) {
        val buf = Net.createPacketBuffer(Net.RECEIVE_BUFFER_CAPACITY)
        try {
            while (isActive) {
                buf.clear()
                dataChannel?.receive(buf)
                buf.flip()
                val header: PacketHeader? = PacketHeader.read(buf)
                when (header?.type) {
                    Net.PacketType.DISCONNECT.value -> processDisconnect()

                    Net.PacketType.AUDIO_DATA_OPUS.value,
                    Net.PacketType.AUDIO_DATA_UNCOMPRESSED.value -> processAudioData(buf)

                    Net.PacketType.SERVER_KEEP_ALIVE.value -> updateServerLastContact()

                    Net.PacketType.ACK.value -> processAck(buf)

                    else -> {}
                }
            }
        } catch (_: AsynchronousCloseException) {
        }
    }

    private fun keepAlive() = scope.launch {
        serverLastContact = System.nanoTime()
        while (isActive) {
            delay(1000L)

            val elapsedNanos = System.nanoTime() - serverLastContact
            val elapsedSeconds = TimeUnit.SECONDS.convert(elapsedNanos, TimeUnit.NANOSECONDS)
            if (elapsedSeconds >= Net.SERVER_TIMEOUT_SECONDS) {
                when (currentStatus) {
                    ConnectionStatus.CONNECTING -> sendMessage(SystemMessage.MESSAGE_CONNECT_FAILED)
                    ConnectionStatus.CONNECTED -> sendMessage(SystemMessage.MESSAGE_DISCONNECTED)
                    else -> Unit
                }
                shutdown()
            }

            send(Net.getKeepAlivePacket())
        }
    }

    private fun updateServerLastContact() {
        if (currentStatus != ConnectionStatus.CONNECTED) return
        serverLastContact = System.nanoTime()
    }

    private suspend fun processAudioData(buffer: ByteBuffer) {
        if (currentStatus != ConnectionStatus.CONNECTED || !processAudio) return
        val packetData = ByteArray(buffer.remaining())
        buffer.get(packetData)
        audioDataSink.send(packetData)
        updateServerLastContact()
    }

    private fun processAck(buffer: ByteBuffer) {
        if (pendingRequests.isEmpty()) return
        val ackData = AckData.read(buffer) ?: return
        val i = pendingRequests.iterator()
        while (i.hasNext()) {
            val (type, request) = i.next()
            if (request.id == ackData.requestId) {
                when (type) {
                    Net.PacketType.CONNECT -> processAckConnect(buffer)

                    // TODO: Process format change acknowledgement
                    Net.PacketType.SET_FORMAT -> {}

                    else -> {}
                }
                i.remove()
                return
            }
        }
    }

    /**
     * Process ACK response on a Connect request.
     * @param buffer [ByteBuffer] must be positioned on ACK packet custom data.
     */
    private fun processAckConnect(buffer: ByteBuffer) {
        if (currentStatus == ConnectionStatus.CONNECTING) {
            updateStatus(ConnectionStatus.CONNECTED)
        }
        val ackConnectResponse = AckConnectData.read(buffer)
        if (ackConnectResponse != null) {
            serverProtocol = ackConnectResponse.protocol
        }
    }

    private fun processDisconnect() {
        scope.launch { shutdown() }
    }
}

private data class Request(
    val id: UShort = Random.nextInt(0, UShort.MAX_VALUE.toInt()).toUShort(),
    val sentAt: Long = System.nanoTime()
)