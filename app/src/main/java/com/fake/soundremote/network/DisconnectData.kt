package com.fake.soundremote.network

import java.nio.ByteBuffer

class DisconnectData : PacketData {
    override fun write(dest: ByteBuffer) {}

    override val size: Int
        get() = 0
}
