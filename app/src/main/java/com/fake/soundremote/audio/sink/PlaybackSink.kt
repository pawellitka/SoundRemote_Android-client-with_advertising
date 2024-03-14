package com.fake.soundremote.audio.sink

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import androidx.annotation.RequiresApi
import com.fake.soundremote.BuildConfig
import timber.log.Timber

private const val SAMPLE_RATE = 48_000
private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO
private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
private const val FRAME_SIZE = 4

/** Multiplication factor to apply to the minimum buffer size requested. */
private const val PCM_BUFFER_MULTIPLICATION_FACTOR = 4

/** Minimum length for the AudioTrack buffer, in milliseconds. */
private const val MIN_PCM_BUFFER_DURATION = 250

/** Maximum length for the AudioTrack buffer, in milliseconds. */
private const val MAX_PCM_BUFFER_DURATION = 750

/** Minimum size for the AudioTrack buffer, in bytes. */
private const val MIN_BUFFER_SIZE = MIN_PCM_BUFFER_DURATION * SAMPLE_RATE * FRAME_SIZE / 1000

/** Maximum size for the AudioTrack buffer, in bytes. */
private const val MAX_BUFFER_SIZE = MAX_PCM_BUFFER_DURATION * SAMPLE_RATE * FRAME_SIZE / 1000

class PlaybackSink {
    private val sessionId = AudioManager.AUDIO_SESSION_ID_GENERATE
    private val audioTrack = createTrack()
    private var underruns = 0

    fun start() {
        audioTrack.play()
    }

    fun play(audioData: ByteArray, dataSize: Int) {
        val writeResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioTrack.write(audioData, 0, dataSize, AudioTrack.WRITE_NON_BLOCKING)
        } else {
            audioTrack.write(audioData, 0, dataSize)
        }
        if (writeResult < 0) {
            throw IllegalStateException("AudioTrack write error: $writeResult")
        }
        if (BuildConfig.DEBUG && writeResult != dataSize) {
            Timber.i("AudioTrack underwrite: $writeResult written out of $dataSize")
        }
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val currentUnderruns = audioTrack.underrunCount
            if (currentUnderruns > underruns) {
                underruns = currentUnderruns
                Timber.i("AudioTrack underruns: $currentUnderruns")
            }
        }
    }

    fun stop() {
        audioTrack.stop()
    }

    fun release() {
        try {
            audioTrack.pause()
            audioTrack.flush()
        } finally {
            audioTrack.release()
        }
    }

    private fun createTrack(): AudioTrack {
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, ENCODING)
        val bufferSize = getBufferSizeInBytes(minBufferSize)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(ENCODING)
            .setChannelMask(CHANNEL_CONFIG)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAudioTrackV26(audioAttributes, audioFormat, bufferSize)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createAudioTrackV23(audioAttributes, audioFormat, bufferSize)
        } else {
            createAudioTrackV21(audioAttributes, audioFormat, bufferSize)
        }
    }

    private fun getBufferSizeInBytes(minBufferSizeInBytes: Int): Int {
        val targetBufferSize = minBufferSizeInBytes * PCM_BUFFER_MULTIPLICATION_FACTOR
        return targetBufferSize.coerceIn(MIN_BUFFER_SIZE..MAX_BUFFER_SIZE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAudioTrackV26(
        audioAttributes: AudioAttributes,
        audioFormat: AudioFormat,
        bufferSize: Int,
    ): AudioTrack = AudioTrack.Builder()
        .setAudioAttributes(audioAttributes)
        .setAudioFormat(audioFormat)
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .setSessionId(sessionId)
        .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
        .build()

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createAudioTrackV23(
        audioAttributes: AudioAttributes,
        audioFormat: AudioFormat,
        bufferSize: Int,
    ): AudioTrack = AudioTrack.Builder()
        .setAudioAttributes(audioAttributes)
        .setAudioFormat(audioFormat)
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .setSessionId(sessionId)
        .build()

    private fun createAudioTrackV21(
        audioAttributes: AudioAttributes,
        audioFormat: AudioFormat,
        bufferSize: Int,
    ): AudioTrack {
        return AudioTrack(
            audioAttributes,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM,
            sessionId,
        )
    }
}
