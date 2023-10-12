package com.fake.soundremote.audio.decoder

import com.fake.jopus.OPUS_OK
import com.fake.jopus.Opus

/**
 * Creates new OpusAudioDecoder
 * @param sampleRate Sample rate in Hz, must be 8/12/16/24/48 KHz
 * @param channels   Number of channels, must be 1 or 2
 */
class OpusAudioDecoder(
    private val sampleRate: Int = DEFAULT_SAMPLE_RATE,
    private val channels: Int = DEFAULT_CHANNELS
) {
    private var sampleSize = DEFAULT_SAMPLE_SIZE
    private val opus = Opus()

    fun outBufferSize(): Int {
        return MAX_SAMPLES_PER_PACKET * channels * sampleSize
    }

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
        val samplesDecoded = opus.decode(encodedData, dataSize, outPcm, MAX_SAMPLES_PER_PACKET, 0)
        if (samplesDecoded < 0) {
            val errorString = opus.strerror(samplesDecoded)
            throw DecoderException("Opus decode error: $errorString")
        }
        return samplesDecoded * channels * sampleSize
    }

    companion object {
        private const val DEFAULT_SAMPLE_RATE = 48000
        private const val DEFAULT_CHANNELS = 2

        // Sample size in bytes (16 bit signed)
        private const val DEFAULT_SAMPLE_SIZE = 2

        // Maximum number of samples per channel in output buffer (120ms; 48khz)
        private const val MAX_SAMPLES_PER_PACKET = 5760
    }
}