package com.fake.soundremote.audio.sink

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build

class PlaybackSink {
    private val audioTrack: AudioTrack

    init {
        audioTrack = createTrack()
    }

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
            throw IllegalStateException("PlaybackSink: AudioTrack.write error $writeResult")
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
        val sampleRate = 48000
        val channels = AudioFormat.CHANNEL_OUT_STEREO
        val format = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channels, format) * 2
        val mode = AudioTrack.MODE_STREAM
        val sessionId = 1
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val audioFormat = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(format)
            .setChannelMask(channels)
            .build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(mode)
                .setSessionId(sessionId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                AudioManager.isOffloadedPlaybackSupported(audioFormat, audioAttributes)
            ) {
                builder.setOffloadedPlayback(true)
            } else {
                builder.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            }
            builder.build()
        } else {
            AudioTrack(audioAttributes, audioFormat, bufferSize, mode, sessionId)
        }
    }
}