package com.fake.soundremote.util

import com.fake.soundremote.util.Net.uByte
import com.fake.soundremote.util.Net.uInt
import com.fake.soundremote.util.Net.uShort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

@DisplayName("Network utils")
internal class NetworkTest {

    @Nested
    @DisplayName("ByteBuffer.uByte")
    inner class GetUByte {
        @Test
        @DisplayName("reads correctly")
        fun readsCorrectly() {
            val expected: UByte = 0xFAu
            val buf = ByteBuffer.allocate(8)
            buf.put(expected.toByte())
            buf.rewind()

            val actual = buf.uByte

            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("increments the ByteBuffer position by 1")
        fun incrementsPositionCorrectly() {
            val buf = ByteBuffer.allocate(8)

            buf.uByte

            assertEquals(1, buf.position())
        }
    }

    @Nested
    @DisplayName("ByteBuffer.uShort")
    inner class GetUShort {

        @Test
        @DisplayName("reads correctly")
        fun readsCorrectly() {
            val expected: UShort = 0xFA_01u
            val buf = ByteBuffer.allocate(8)
            buf.putShort(expected.toShort())
            buf.rewind()

            val actual = buf.uShort

            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("increments the ByteBuffer position by 2")
        fun incrementsPositionCorrectly() {
            val buf = ByteBuffer.allocate(8)

            buf.uShort

            assertEquals(2, buf.position())
        }
    }

    @Nested
    @DisplayName("ByteBuffer.uInt")
    inner class GetUInt {

        @Test
        @DisplayName("reads correctly")
        fun readsCorrectly() {
            val expected = 0xFA_01_02_03u
            val buf = ByteBuffer.allocate(8)
            buf.putInt(expected.toInt())
            buf.rewind()

            val actual = buf.uInt

            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("increments the ByteBuffer position by 4")
        fun incrementsPositionCorrectly() {
            val buf = ByteBuffer.allocate(8)

            buf.uInt

            assertEquals(4, buf.position())
        }
    }
}
