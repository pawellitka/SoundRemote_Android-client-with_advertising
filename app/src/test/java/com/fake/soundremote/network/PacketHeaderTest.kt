package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.ByteBuffer
import java.util.stream.Stream

@DisplayName("PacketHeader")
internal class PacketHeaderTest {

    @DisplayName("readHeader")
    @Nested
    inner class ReadHeaderTests {

        @DisplayName("Returns null given invalid datagram")
        @ParameterizedTest
        @MethodSource("com.fake.soundremote.network.PacketHeaderTest#invalidDatagramProvider")
        fun invalidDatagram_ReturnsNull(buffer: ByteBuffer) {
            val actual = PacketHeader.read(buffer)

            assertNull(actual)
        }

        @DisplayName("Returns correct packet header given valid datagram")
        @ParameterizedTest
        @MethodSource("com.fake.soundremote.network.PacketHeaderTest#validDatagramProvider")
        fun validDatagram_ReturnsCorrectHeader(buffer: ByteBuffer, expected: PacketHeader) {
            val actual = PacketHeader.read(buffer)

            assertEquals(expected, actual)
        }
    }

    companion object {
        @JvmStatic
        fun invalidDatagramProvider(): Stream<Arguments> {
            return Stream.of(
                // Empty buffer
                Arguments.arguments(generateEmptyByteBuffer(0)),
                // Size less than header size
                Arguments.arguments(
                    generateEmptyByteBuffer(PacketHeader.SIZE - 1)
                        .putChar(Net.PROTOCOL_SIGNATURE)
                ),
                // Mismatched size field and packet size
                Arguments.arguments(
                    generateByteBuffer(
                        capacity = 100,
                        signature = Net.PROTOCOL_SIGNATURE,
                        type = 20.toByte(),
                        size = 101.toChar()
                    )
                ),
                // Incorrect signature
                Arguments.arguments(
                    generateByteBuffer(
                        capacity = 50,
                        signature = Net.PROTOCOL_SIGNATURE + 1,
                        type = 20.toByte(),
                        size = 50.toChar()
                    )
                ),
            )
        }

        @JvmStatic
        fun validDatagramProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(
                    generateByteBuffer(
                        capacity = 100,
                        signature = Net.PROTOCOL_SIGNATURE,
                        type = 20.toByte(),
                        size = 100.toChar()
                    ), PacketHeader(20, 100)
                ),
                // Type byte value > Byte.MAX_VALUE
                Arguments.arguments(
                    generateByteBuffer(
                        capacity = 50,
                        signature = Net.PROTOCOL_SIGNATURE,
                        type = 200.toByte(),
                        size = 50.toChar()
                    ), PacketHeader(200, 50)
                ),
            )
        }

        private fun generateEmptyByteBuffer(capacity: Int): ByteBuffer {
            return Net.createPacketBuffer(capacity)
        }

        private fun generateByteBuffer(
            capacity: Int,
            signature: Char,
            type: Byte,
            size: Char
        ): ByteBuffer {
            val result = Net.createPacketBuffer(capacity)
                .putChar(signature)
                .put(type)
                .putChar(size)
            result.rewind()
            return result
        }
    }
}
