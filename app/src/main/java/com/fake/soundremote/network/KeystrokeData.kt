package com.fake.soundremote.network

import java.nio.ByteBuffer

/*
signed 32bit    Virtual-key code
signed 32bit    Bit field of the mod keys.
*/
private const val SIZE = 8

data class KeystrokeData(val keyCode: Int, val mods: Int) : PacketData {

    override fun writeToBuffer(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.putInt(keyCode)
        dest.putInt(mods)
    }

    override val size: Int
        get() = SIZE
}
