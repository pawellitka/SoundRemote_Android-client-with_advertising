package com.fake.soundremote.audio

import androidx.annotation.IntDef
import com.fake.soundremote.audio.decoder.OpusAudioDecoder
import com.fake.soundremote.audio.sink.PlaybackSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPipe(private val receivedAudio: ReceiveChannel<ByteArray>) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val decoder = OpusAudioDecoder()
    private val playback = PlaybackSink()
    private val decodedData = ByteArray(decoder.outBufferSize())

    private var playJob: Job? = null
    private var stopJob: Job? = null
    private val playLock = Any()
    private val stopLock = Any()

    var audioCompressed = true

    @PipeState
    var state: Int = PIPE_STOPPED
        private set

    fun start() {
        if (state == PIPE_RELEASED) {
            throw IllegalStateException("Can't start(): AudioPipe is released")
        }
        synchronized(playLock) {
            if (playJob?.isActive == true) return
            playJob = scope.launch {
                stopJob?.join()
                state = PIPE_PLAYING
                playback.start()
                while (isActive) {
                    val audio = receivedAudio.receive()
                    if (audioCompressed) {
                        val decodedBytes = decoder.decode(audio, decodedData)
                        playback.play(decodedData, decodedBytes)
                    } else {
                        playback.play(audio, audio.size)
                    }
                }
            }
        }
    }

    fun stop() {
        if (state == PIPE_RELEASED) {
            throw IllegalStateException("Can't stop(): AudioPipe is released")
        }
        synchronized(stopLock) {
            if (stopJob?.isActive == true) return
            stopJob = scope.launch {
                playJob?.cancelAndJoin()
                state = PIPE_STOPPED
                playback.stop()
            }
        }
    }

    fun release() {
        state = PIPE_RELEASED
        scope.launch {
            playJob?.cancelAndJoin()
            playJob = null
            decoder.release()
            playback.release()
        }
    }

    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(PIPE_PLAYING, PIPE_STOPPED, PIPE_RELEASED)
        annotation class PipeState

        internal const val PIPE_PLAYING = 1
        internal const val PIPE_STOPPED = 2
        internal const val PIPE_RELEASED = 3
    }
}