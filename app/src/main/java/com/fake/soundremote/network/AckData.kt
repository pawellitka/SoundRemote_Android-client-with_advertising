package com.fake.soundremote.network

import com.fake.soundremote.network.PacketHeader.Companion.SIZE
import com.fake.soundremote.util.Net.getUShort
import java.nio.ByteBuffer

data class AckData(val requestId: UShort) {
    companion object {
        /*
        unsigned 16bit   request id
        */
        private const val SIZE = 2

        /**
         * Read ACK packet data from the source [ByteBuffer].
         * Increments [source] position by [SIZE] on successful read.
         * @param source [ByteBuffer] to read from
         * @return [AckData] instance or null if there is not enough data remaining in [source].
         */
        fun read(source: ByteBuffer): AckData? {
            if (source.remaining() < SIZE) return null
            val id = source.getUShort()
            return AckData(id)
        }
    }
}