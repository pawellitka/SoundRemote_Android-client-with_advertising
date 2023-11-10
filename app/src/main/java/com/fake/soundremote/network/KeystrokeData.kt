package com.fake.soundremote.network

import java.nio.ByteBuffer

data class KeystrokeData(val keyCode: Int, val mods: Int) : PacketData {

    override fun write(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.put(keyCode.toByte())
        dest.put(mods.toByte())
    }

    companion object {
        /**
         * unsigned 8bit    Virtual-key code
         *
         * unsigned 8bit    Bit field of the mod keys
         */
        const val SIZE = 2
    }
}
