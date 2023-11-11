package com.fake.soundremote.network

import android.os.Build
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.Net
import com.fake.soundremote.util.PacketProtocolType
import com.fake.soundremote.util.SystemMessage
import kotlinx.coroutines.CoroutineDispatcher
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
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.AlreadyBoundException
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.DatagramChannel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

internal class Connection(
    private val uncompressedAudio: SendChannel<ByteArray>,
    private val opusAudio: SendChannel<ByteArray>,
    private val connectionMessages: SendChannel<SystemMessage>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var receiveJob: Job? = null
    private var keepAliveJob: Job? = null
    private var pendingRequests = mutableMapOf<Net.PacketCategory, Request>()

    private var serverAddress: InetSocketAddress? = null
    private var dataChannel: DatagramChannel? = null
    private var sendChannel: DatagramChannel? = null
    private val sendLock = Any()

    private var serverProtocol: PacketProtocolType = 1u
    private var serverLastContact = AtomicLong(0)
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
    ): Boolean = withContext(dispatcher) {
        shutdown()

        updateStatus(ConnectionStatus.CONNECTING)
        try {
            synchronized(sendLock) {
                serverAddress = InetSocketAddress(address, serverPort)
                sendChannel = createSendChannel()
            }
            dataChannel = createReceiveChannel(InetSocketAddress(localPort))
        } catch (e: IllegalStateException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && e is AlreadyBoundException) {
                sendMessage(SystemMessage.MESSAGE_ALREADY_BOUND)
            } else {
                sendMessage(SystemMessage.MESSAGE_BIND_ERROR)
            }
            shutdown()
            return@withContext false
        } catch (e: Exception) {
            sendMessage(SystemMessage.MESSAGE_BIND_ERROR)
            shutdown()
            return@withContext false
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

    private suspend fun shutdown() = withContext(dispatcher) {
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
        pendingRequests[Net.PacketCategory.CONNECT] = request
    }

    fun sendSetFormat(@Net.Compression compression: Int) {
        val request = Request()
        val packet = Net.getSetFormatPacket(compression, request.id)
        scope.launch { send(packet) }
        pendingRequests[Net.PacketCategory.SET_FORMAT] = request
    }

    fun sendKeystroke(keyCode: Int, mods: Int) {
        val keystrokePacket = Net.getKeystrokePacket(keyCode.toUByte(), mods.toUByte())
        scope.launch { send(keystrokePacket) }
    }

    private suspend fun send(data: ByteBuffer) = withContext(dispatcher) {
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
                when (header?.category) {
                    Net.PacketCategory.DISCONNECT.value -> processDisconnect()
                    Net.PacketCategory.AUDIO_DATA_OPUS.value -> processAudioData(buf, false)
                    Net.PacketCategory.AUDIO_DATA_UNCOMPRESSED.value -> processAudioData(buf, true)
                    Net.PacketCategory.SERVER_KEEP_ALIVE.value -> updateServerLastContact()
                    Net.PacketCategory.ACK.value -> processAck(buf)
                    else -> {}
                }
            }
        } catch (_: AsynchronousCloseException) {
        }
    }

    private fun keepAlive() = scope.launch {
        serverLastContact.set(System.nanoTime())
        while (isActive) {
            delay(1000L)

            val elapsedNanos = System.nanoTime() - serverLastContact.get()
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
        serverLastContact.set(System.nanoTime())
    }

    private suspend fun processAudioData(buffer: ByteBuffer, uncompressed: Boolean) {
        if (currentStatus != ConnectionStatus.CONNECTED || !processAudio) return
        val packetData = ByteArray(buffer.remaining())
        buffer.get(packetData)
        if (uncompressed) {
            uncompressedAudio.send(packetData)
        } else {
            opusAudio.send(packetData)
        }
        updateServerLastContact()
    }

    private fun processAck(buffer: ByteBuffer) {
        if (pendingRequests.isEmpty()) return
        val ackData = AckData.read(buffer) ?: return
        val i = pendingRequests.iterator()
        while (i.hasNext()) {
            val (category, request) = i.next()
            if (request.id == ackData.requestId) {
                when (category) {
                    Net.PacketCategory.CONNECT -> processAckConnect(ackData.customData)

                    // TODO: Process format change acknowledgement
                    Net.PacketCategory.SET_FORMAT -> {}

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

    private fun createSendChannel(): DatagramChannel {
        return DatagramChannel.open()
    }

    /**
     * Creates a bound [DatagramChannel]
     *
     * @param  bindAddress Address to bind to
     *
     * @throws AlreadyBoundException
     * @throws SecurityException
     * @throws IOException
     */
    private fun createReceiveChannel(bindAddress: InetSocketAddress): DatagramChannel {
        val channel = DatagramChannel.open()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true)
            channel.bind(bindAddress)
        } else {
            channel.socket()?.bind(bindAddress)
        }
        return channel
    }
}

private data class Request(
    val id: UShort = Random.nextInt(0, UShort.MAX_VALUE.toInt()).toUShort(),
    val sentAt: Long = System.nanoTime()
)
