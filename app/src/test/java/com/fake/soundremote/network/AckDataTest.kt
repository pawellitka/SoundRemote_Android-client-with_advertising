package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.putUShort
import com.fake.soundremote.util.PacketRequestIdType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("AckData")
internal class AckDataTest {
    @DisplayName("Reads correctly")
    @Test
    fun ackData_ReadsCorrectly() {
        val requestId: PacketRequestIdType = 0xFAAFu
        val customData = 0xFA123456.toInt()
        val expectedCustomData = Net.createPacketBuffer(AckData.CUSTOM_DATA_SIZE)
            .putInt(customData)
        expectedCustomData.rewind()
        val expected = AckData(requestId, expectedCustomData)
        val packet = Net.createPacketBuffer(20)
            .putUShort(requestId)
            .putInt(customData)
        packet.rewind()

        val actual = AckData.read(packet)

        Assertions.assertNotNull(actual)
        Assertions.assertEquals(expected, actual!!)
    }
}