package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.putUByte
import com.fake.soundremote.util.Net.putUShort
import com.fake.soundremote.util.PacketRequestIdType
import java.nio.ByteBuffer

data class SetFormatData(
    @Net.Compression val compression: Int,
    val requestId: PacketRequestIdType
) : PacketData {
    override fun write(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.putUShort(requestId)
        dest.putUByte(compression.toUByte())
    }

    companion object {
        /**
         * unsigned 16bit   Request id
         *
         * unsigned 8bit    Compression
         */
        const val SIZE = 3
    }
}
