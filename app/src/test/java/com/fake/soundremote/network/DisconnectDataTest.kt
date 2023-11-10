package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("DisconnectData")
internal class DisconnectDataTest {

    @DisplayName("size has correct value")
    @Test
    fun size_ReturnsCorrectValue() {
        val expected = 0

        val actual = DisconnectData().size

        assertEquals(expected, actual)
    }

    @DisplayName("write() writes correctly")
    @Test
    fun write_WritesCorrectly() {
        val disconnectData = DisconnectData()
        val expected = Net.createPacketBuffer(disconnectData.size)
        expected.rewind()

        val actual = Net.createPacketBuffer(disconnectData.size)
        disconnectData.write(actual)
        actual.rewind()

        assertEquals(expected, actual)
    }
}
