package com.fake.soundremote.network

import com.fake.soundremote.network.PacketHeader.Companion.SIZE
import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.getUShort
import java.nio.ByteBuffer

data class AckData(val requestId: UShort, val customData: ByteBuffer) {
    companion object {
        const val CUSTOM_DATA_SIZE = 4

        /*
        unsigned 16bit   request id
        4 byte           custom data
        */
        private const val SIZE = 6

        /**
         * Read ACK packet data from the source [ByteBuffer].
         * Increments [source] position by [SIZE] on successful read.
         * @param source [ByteBuffer] to read from
         * @return [AckData] instance or null if there is not enough data remaining in [source].
         */
        fun read(source: ByteBuffer): AckData? {
            if (source.remaining() < SIZE) return null
            val id = source.getUShort()
            val customData = Net.createPacketBuffer(CUSTOM_DATA_SIZE)
            source.get(customData.array())
            return AckData(id, customData)
        }
    }
}
