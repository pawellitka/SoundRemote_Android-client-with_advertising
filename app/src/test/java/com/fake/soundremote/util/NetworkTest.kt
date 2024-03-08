package com.fake.soundremote.util

import com.fake.soundremote.util.Net.uInt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

@DisplayName("Network utils")
internal class NetworkTest {

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
        @DisplayName("advances ByteBuffer position by 4")
        fun advancesCorrectly() {
            val buf = ByteBuffer.allocate(8)
            buf.putInt(1)
            buf.rewind()

            buf.uInt

            assertEquals(4, buf.position())
        }
    }
}
