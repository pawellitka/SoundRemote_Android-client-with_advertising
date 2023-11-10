package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.putUByte
import com.fake.soundremote.util.Net.putUShort
import com.fake.soundremote.util.PacketRequestIdType
import java.nio.ByteBuffer

/*
unsigned 16bit   request id
unsigned 8bit    compression
*/
private const val SIZE = 3

data class SetFormatData(
    @Net.Compression val compression: Int,
    val requestId: PacketRequestIdType
) :
    PacketData {
    override fun write(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.putUShort(requestId)
        dest.putUByte(compression.toUByte())
    }

    override val size: Int
        get() = SIZE
}
