package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

@DisplayName("Connection")
class ConnectionTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @DisplayName("connect() calls")
    @Test
    fun connect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val connection = Connection(Channel(), Channel(), Channel(), dispatcher)
        val sendChannel = mock<DatagramChannel>()
        val receiveChannel = mock<DatagramChannel>()
        val connectionSpy = spy(connection) {
            on { createSendChannel() } doReturn sendChannel
            on { createReceiveChannel(bindAddress) } doReturn receiveChannel
        }

        connectionSpy.connect(address, serverPort, localPort, Net.COMPRESSION_320)

        verify(sendChannel)
            .send(any(ByteBuffer::class.java), eq(serverAddress))
    }

    companion object {
        private const val address = "123.45.67.89"
        private const val serverPort = 30_000
        private const val localPort = 40_000
        private val bindAddress = InetSocketAddress(localPort)
        private val serverAddress = InetSocketAddress(address, serverPort)
    }
}
