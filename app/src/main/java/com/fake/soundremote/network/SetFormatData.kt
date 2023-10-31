package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import java.nio.ByteBuffer

/*
unsigned 8bit    compression
*/
private const val SIZE = 1

data class SetFormatData(@Net.Compression val compression: Int) : PacketData {
    override fun writeToBuffer(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.put(compression.toByte())
    }

    override val size: Int
        get() = SIZE
}
