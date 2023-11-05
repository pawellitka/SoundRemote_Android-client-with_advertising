package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import com.fake.soundremote.util.Net.putUByte
import com.fake.soundremote.util.Net.putUShort
import java.nio.ByteBuffer

/*
unsigned 8bit    protocol version
unsigned 16bit   request id
unsigned 8bit    compression
*/
private const val SIZE = 4

data class ConnectData(@Net.Compression val compression: Int, val requestId: UShort) : PacketData {
    override fun write(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.putUByte(Net.PROTOCOL_VERSION)
        dest.putUShort(requestId)
        dest.putUByte(compression.toUByte())
    }

    override val size: Int
        get() = SIZE
}
