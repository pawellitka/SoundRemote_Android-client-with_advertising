package com.fake.soundremote.network

import com.fake.soundremote.network.Connection.Companion.createReceiveChannel
import com.fake.soundremote.network.Connection.Companion.createSendChannel
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.Net
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

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
