package com.fake.soundremote.audio.decoder

import com.fake.jopus.OPUS_OK
import com.fake.jopus.Opus
import com.fake.soundremote.util.Audio.CHANNELS
import com.fake.soundremote.util.Audio.FRAME_DURATION
import com.fake.soundremote.util.Audio.SAMPLE_RATE
import com.fake.soundremote.util.Audio.SAMPLE_SIZE

/**
 * Creates new OpusAudioDecoder
 * @param sampleRate sample rate in Hz, must be 8/12/16/24/48 KHz
 * @param channels number of channels, must be 1 or 2
 * @param frameDuration frame duration in microseconds, must be a multiple of 2.5ms up to 60ms
 */
class OpusAudioDecoder(
    private val sampleRate: Int = SAMPLE_RATE,
    private val channels: Int = CHANNELS,
    private val frameDuration: Int = FRAME_DURATION,
) {
    private val opus = Opus()
    private var sampleSize = SAMPLE_SIZE
    private val samplesPerPacket = (sampleRate.toLong() * frameDuration / 1_000_000).toInt()

    /** Bytes per decoded packet */
    val outBufferSize = samplesPerPacket * channels * sampleSize

    init {
        val initResult = opus.initDecoder(sampleRate, channels)
        if (initResult != OPUS_OK) {
            val errorString = opus.strerror(initResult)
            throw DecoderException("Opus decoder init error: $errorString")
        }
    }

    fun release() {
        opus.releaseDecoder()
    }

    fun decode(encodedData: ByteArray, outPcm: ByteArray): Int {
        val dataSize = encodedData.size
        val samplesDecoded = opus.decode(encodedData, dataSize, outPcm, samplesPerPacket, 0)
        if (samplesDecoded < 0) {
            val errorString = opus.strerror(samplesDecoded)
            throw DecoderException("Opus decode error: $errorString")
        }
        return samplesDecoded * channels * sampleSize
    }
}
