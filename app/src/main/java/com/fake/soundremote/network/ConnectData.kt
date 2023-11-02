package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import java.nio.ByteBuffer

/*
unsigned 8bit    compression
unsigned 16bit   request id
*/
private const val SIZE = 3

data class ConnectData(@Net.Compression val compression: Int, val requestId: UShort) : PacketData {
    override fun writeToBuffer(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.put(compression.toByte())
        dest.putShort(requestId.toShort())
    }

    override val size: Int
        get() = SIZE
}
