package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.putUByte
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("AckConnectData")
internal class AckConnectDataTest {
    @DisplayName("Reads correctly")
    @Test
    fun ackConnectData_ReadsCorrectly() {
        val expected: UByte = 0xFDu
        val buffer = Net.createPacketBuffer(AckData.CUSTOM_DATA_SIZE)
            .putUByte(expected)
        buffer.rewind()

        val ackConnectData = AckConnectData.read(buffer)
        val actual = ackConnectData?.protocol

        Assertions.assertNotNull(actual)
        Assertions.assertEquals(expected, actual!!)
    }
}
