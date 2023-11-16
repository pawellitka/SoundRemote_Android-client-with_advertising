package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.COMPRESSION_320
import com.fake.soundremote.util.Net.putUByte
import com.fake.soundremote.util.Net.putUShort
import com.fake.soundremote.util.PacketRequestIdType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ConnectData")
internal class ConnectDataTest {
    @DisplayName("SIZE has correct value")
    @Test
    fun size_ReturnsCorrectValue() {
        val expected = 4

        val actual = ConnectData.SIZE

        Assertions.assertEquals(expected, actual)
    }

    @DisplayName("Writes correctly")
    @Test
    fun write_WritesCorrectly() {
        val requestId: PacketRequestIdType = 0xFAAFu
        @Net.Compression val compression = COMPRESSION_320
        val expected = Net.createPacketBuffer(ConnectData.SIZE)
        expected.putUByte(Net.PROTOCOL_VERSION)
        expected.putUShort(requestId)
        expected.putUByte(compression.toUByte())
        expected.rewind()

        val actual = Net.createPacketBuffer(ConnectData.SIZE)
        ConnectData(compression, requestId).write(actual)
        actual.rewind()

        Assertions.assertEquals(expected, actual)
    }
}
