package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("KeepAliveData")
class KeepAliveDataTest {

    @DisplayName("size has correct value")
    @Test
    fun size_ReturnsCorrectValue() {
        val expected = 0

        val actual = KeepAliveData().size

        assertEquals(expected, actual)
    }

    @DisplayName("write() writes correctly")
    @Test
    fun write_WritesCorrectly() {
        val keepAliveData = KeepAliveData()
        val expected = Net.createPacketBuffer(keepAliveData.size)
        expected.rewind()

        val actual = Net.createPacketBuffer(keepAliveData.size)
        keepAliveData.write(actual)
        actual.rewind()

        assertEquals(expected, actual)
    }

}
