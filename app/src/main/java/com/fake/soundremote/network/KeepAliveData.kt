package com.fake.soundremote.network

import java.nio.ByteBuffer

class KeepAliveData : PacketData {
    override fun writeToBuffer(dest: ByteBuffer) {}

    override val size: Int
        get() = 0
}
