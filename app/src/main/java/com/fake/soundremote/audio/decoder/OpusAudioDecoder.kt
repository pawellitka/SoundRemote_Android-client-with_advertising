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
 * @param frameDuration frame duration in microseconds, must be a multiple of 2.5ms from 2.5ms to 60ms
 */
class OpusAudioDecoder(
    private val sampleRate: Int = SAMPLE_RATE,
    private val channels: Int = CHANNELS,
    private val frameDuration: Int = FRAME_DURATION,
) {
    private val opus = Opus()
    private val sampleSize = SAMPLE_SIZE

    /** Number of samples per channel in one frame */
    private val frameSize = (sampleRate.toLong() * frameDuration / 1_000_000).toInt()

    /** Number of bytes per decoded frame */
    val outBufferSize = samplesToBytes(frameSize)

    init {
        check(frameDuration in 2_500..60_000) { "Opus decoder frame duration must be from from 2.5 ms to 60 ms" }
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
        val samplesDecoded = opus.decode(encodedData, dataSize, outPcm, frameSize, 0)
        if (samplesDecoded < 0) {
            val errorString = opus.strerror(samplesDecoded)
            throw DecoderException("Opus decode error: $errorString")
        }
        return samplesToBytes(samplesDecoded)
    }

    private fun samplesToBytes(samples: Int): Int {
        return samples * channels * sampleSize
    }
}
