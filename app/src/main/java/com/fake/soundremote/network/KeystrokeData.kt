package com.fake.soundremote.network

import java.nio.ByteBuffer

/*
unsigned 8bit    Virtual-key code
unsigned 8bit    Bit field of the mod keys
*/
private const val SIZE = 2

data class KeystrokeData(val keyCode: Int, val mods: Int) : PacketData {

    override fun writeToBuffer(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.put(keyCode.toByte())
        dest.put(mods.toByte())
    }

    override val size: Int
        get() = SIZE
}
