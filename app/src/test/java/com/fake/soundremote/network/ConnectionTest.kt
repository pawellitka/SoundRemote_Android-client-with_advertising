package com.fake.soundremote.network

import com.fake.soundremote.network.Connection.Companion.createReceiveChannel
import com.fake.soundremote.network.Connection.Companion.createSendChannel
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.PROTOCOL_VERSION
import com.fake.soundremote.util.Net.getUShort
import com.fake.soundremote.util.Net.putUByte
import com.fake.soundremote.util.Net.putUShort
import com.fake.soundremote.util.PacketRequestIdType
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

// TODO: Check unnecessary stubbing
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
@DisplayName("Connection")
class ConnectionTest {
    @RelaxedMockK
    lateinit var sendChannel: DatagramChannel

    @RelaxedMockK
    lateinit var receiveChannel: DatagramChannel

    @BeforeAll
    fun beforeTests() {
        mockkObject(Connection)
        every { createSendChannel() } returns sendChannel
        every { createReceiveChannel(bindAddress) } returns receiveChannel
    }

    @AfterAll
    fun afterTests() {
        unmockkObject(Connection)
    }

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @DisplayName("Has \"disconnected\" status on creation")
    @Test
    fun constructor_CreatesWithDisconnectedStatus() = runTest {
        val expected = ConnectionStatus.DISCONNECTED

        val connection = Connection(Channel(), Channel(), Channel(), this)
        val actual = connection.connectionStatus.value

        assertEquals(expected, actual)
    }

    @DisplayName("Changes status to \"connecting\" on connection attempt")
    @Test
    fun connect_ChangesStatusToConnecting() = runTest {
        val connection = Connection(Channel(), Channel(), Channel(), this.backgroundScope)

        assertEquals(ConnectionStatus.DISCONNECTED, connection.connectionStatus.value)
        val expected = ConnectionStatus.CONNECTING

        connection.connect(address, serverPort, localPort, compression)
        val actual = connection.connectionStatus.value

        assertEquals(expected, actual)
    }

    @DisplayName("Changes status to \"disconnected\" on disconnect after connection attempt")
    @Test
    fun disconnect_ChangesStatusToDisconnected() = runTest {
        val connection = Connection(Channel(), Channel(), Channel(), this.backgroundScope)
        assertEquals(ConnectionStatus.DISCONNECTED, connection.connectionStatus.value)
        connection.connect(address, serverPort, localPort, compression)
        assertEquals(ConnectionStatus.CONNECTING, connection.connectionStatus.value)
        val expected = ConnectionStatus.DISCONNECTED

        connection.disconnect()
        val actual = connection.connectionStatus.value

        assertEquals(expected, actual)
    }

    @DisplayName("connect() sends datagram to the server")
    @Test
    fun connect_SendsDatagram() = runTest {
        val connection = Connection(Channel(), Channel(), Channel(), this.backgroundScope)

        connection.connect(address, serverPort, localPort, compression)

        verify(exactly = 1) { sendChannel.send(any(ByteBuffer::class), serverAddress) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @DisplayName("Changes status after receiving ACK connect datagram and disconnect datagram")
    @Test
    fun receives_AckConnectAndDisconnect() = runTest {
        var requestId: PacketRequestIdType? = null
        // Capture requestId from outgoing connect requests
        val request = slot<ByteBuffer>()
        every { sendChannel.send(capture(request), any()) } answers {
            val result = request.captured.remaining()
            requestId = getConnectRequestId(request.captured)
            result
        }
        // Imitate server response with the captured requestId.
        // After server response, respond with disconnect datagrams
        val response = slot<ByteBuffer>()
        var connectReceived = false
        every { receiveChannel.receive(capture(response)) } answers {
            requestId?.let {
                if (connectReceived) {
                    PacketHeader(Net.PacketCategory.DISCONNECT, PacketHeader.SIZE)
                        .write(response.captured)
                } else {
                    writeConnectResponse(it, response.captured)
                    connectReceived = true
                }
            }
            serverAddress
        }

        val connection = Connection(Channel(), Channel(), Channel(), this)
        val expectedStatuses = listOf(
            ConnectionStatus.DISCONNECTED,
            ConnectionStatus.CONNECTING,
            ConnectionStatus.CONNECTED,
            ConnectionStatus.DISCONNECTED
        )
        var currentExpectedStatus = 0
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            connection.connectionStatus.collect { actual ->
                assertEquals(expectedStatuses[currentExpectedStatus], actual)
                currentExpectedStatus++
            }
        }
        connection.connect(address, serverPort, localPort, compression)
        advanceUntilIdle()
    }

    // Utility
    private fun getConnectRequestId(source: ByteBuffer): PacketRequestIdType? {
        val header = PacketHeader.read(source)
        if (header?.category != Net.PacketCategory.CONNECT.value ||
            source.remaining() < ConnectData.SIZE
        ) {
            return null
        }
        source.get()
        return source.getUShort()
    }

    private fun writeConnectResponse(requestId: PacketRequestIdType, dest: ByteBuffer) {
        val packetSize = AckData.SIZE + PacketHeader.SIZE
        PacketHeader(Net.PacketCategory.ACK, packetSize)
            .write(dest)
        dest.putUShort(requestId)
        dest.putUByte(PROTOCOL_VERSION)
        dest.position(packetSize)
    }

    companion object {
        @Net.Compression
        private const val compression = Net.COMPRESSION_320
        private const val address = "123.45.67.89"
        private const val serverPort = 30_000
        private const val localPort = 40_000
        private val bindAddress = InetSocketAddress(localPort)
        private val serverAddress = InetSocketAddress(address, serverPort)
    }
}
