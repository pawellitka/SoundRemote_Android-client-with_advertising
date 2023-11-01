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

internal class Connection(
    private val audioDataSink: SendChannel<ByteArray>,
    private val connectionMessages: SendChannel<SystemMessage>
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var receiveJob: Job? = null
    private var keepAliveJob: Job? = null
    private var closeJob: Job? = null

    private var serverAddress: InetSocketAddress? = null
    private var dataChannel: DatagramChannel? = null
    private var sendChannel: DatagramChannel? = null
    private val sendLock = Any()

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
            sendConnect(compression)
            receiveJob = receive()
            keepAliveJob = keepAlive()
            true
        }

    fun close() {
        scope.launch { shutdown() }
    }

    private suspend fun shutdown() {
        if (currentStatus == ConnectionStatus.DISCONNECTED) return
        if (closeJob == null) {
            closeJob = scope.launch(Dispatchers.IO) {
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

                receiveJob?.join()
                keepAliveJob?.join()
            }
        }
        closeJob?.join()
        closeJob = null
        updateStatus(ConnectionStatus.DISCONNECTED)
    }

    private fun sendConnect(@Net.Compression compression: Int) {
        val packet = Net.getConnectPacket(compression)
        send(packet)
    }

    fun sendSetFormat(@Net.Compression compression: Int) {
        val packet = Net.getSetFormatPacket(compression)
        send(packet)
    }

    fun sendKeystroke(keyCode: Int, mods: Int) {
        val keystrokePacket = Net.getKeystrokePacket(keyCode, mods)
        send(keystrokePacket)
    }

    private fun send(data: ByteBuffer) = scope.launch(Dispatchers.IO) {
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
                    Net.PacketType.AUDIO_DATA_OPUS.value,
                    Net.PacketType.AUDIO_DATA_UNCOMPRESSED.value -> if (processAudio) {
                        val packetData = ByteArray(buf.remaining())
                        buf.get(packetData)
                        audioDataSink.send(packetData)
                        updateServerLastSeen()
                    }

                    Net.PacketType.SERVER_KEEP_ALIVE.value -> {
                        updateServerLastSeen()
                    }

                    else -> {}
                }
            }
        } catch (_: AsynchronousCloseException) {
        }
    }

    private fun keepAlive() = scope.launch(Dispatchers.IO) {
        serverLastContact = System.nanoTime()
        while (isActive) {
            send(Net.getKeepAlivePacket())

            val elapsedNanos = System.nanoTime() - serverLastContact
            if (TimeUnit.SECONDS.convert(elapsedNanos, TimeUnit.NANOSECONDS) >=
                Net.SERVER_TIMEOUT_SECONDS
            ) {
                when (currentStatus) {
                    ConnectionStatus.CONNECTING -> sendMessage(SystemMessage.MESSAGE_CONNECT_FAILED)
                    ConnectionStatus.CONNECTED -> sendMessage(SystemMessage.MESSAGE_DISCONNECTED)
                    else -> Unit
                }
                close()
            }
            delay(1000L)
        }
    }

    private fun updateServerLastSeen() {
        val newContactTime = System.nanoTime()
        if (currentStatus == ConnectionStatus.CONNECTING) {
            updateStatus(ConnectionStatus.CONNECTED)
        }
        serverLastContact = newContactTime
    }
}
