package com.fake.soundremote.util

import android.media.AudioFormat

object Audio {
    const val SAMPLE_RATE = 48_000
    const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO
    const val CHANNELS = 2
    const val SAMPLE_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    const val SAMPLE_SIZE = 2
    const val PACKET_DURATION = 10_000
}
