package com.fake.soundremote.network

import com.fake.soundremote.util.Net
import java.nio.ByteBuffer

data class PacketHeader(val type: Int, val packetSize: Int) {
    constructor(type: Net.PacketType, packetSize: Int) : this(type.value, packetSize)

    /**
     * Writes this header to the given ByteBuffer and increments its position by [SIZE].
     * @param dest [ByteBuffer] to write to.
     * @throws IllegalArgumentException if there are fewer than [SIZE] bytes remaining in [dest].
     */
    fun write(dest: ByteBuffer) {
        require(dest.remaining() >= SIZE)
        dest.putChar(Net.PROTOCOL_SIGNATURE)
        dest.put(type.toByte())
        dest.putChar(packetSize.toChar())
    }

    companion object {
        /*
        unsigned 16bit    protocol signature
        unsigned 8bit     packet type
        unsigned 16bit    packet size including header
        */
        /**
         *  Network packet header size in bytes
         */
        const val SIZE = 5

        /**
         * Read a [PacketHeader] from the source ByteBuffer which must contain exactly one datagram.
         * This method checks protocol signature and packet size. Increments [buffer] position by
         * [SIZE] on successful read, or by an arbitrary value on fail.
         * @param buffer [ByteBuffer] to read from
         * @return The header or null if the [buffer] doesn't contain a single datagram with a valid
         * header
         */
        fun read(buffer: ByteBuffer): PacketHeader? {
            if (buffer.remaining() < SIZE) return null
            val signature = buffer.char
            if (signature != Net.PROTOCOL_SIGNATURE) return null
            val type = Net.readUByte(buffer)
            val packetSize = buffer.char
            val header = PacketHeader(type, packetSize.code)
            return if (buffer.limit() != header.packetSize) null else header
        }
    }
}
