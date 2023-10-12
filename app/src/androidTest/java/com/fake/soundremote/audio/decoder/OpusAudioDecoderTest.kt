package com.fake.soundremote.audio.decoder

import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Enclosed::class)
internal class OpusAudioDecoderTest {

    @RunWith(Parameterized::class)
    internal class ValidArgs_CreatesSuccessfully(private val rate: Int, private val channels: Int) {
        companion object {
            @JvmStatic
            @Parameters(name = "{0} Hz, {1} channel(s)")
            fun data(): Collection<Array<Any>> {
                return listOf(arrayOf(8000, 1), arrayOf(24000, 1), arrayOf(48000, 2))
            }
        }

        @Test
        fun test() {
            OpusAudioDecoder(rate, channels)
        }
    }

    @RunWith(Parameterized::class)
    internal class InvalidArgs_ThrowsDecoderException(
        private val rate: Int,
        private val channels: Int
    ) {
        companion object {
            @JvmStatic
            @Parameters(name = "{0} Hz, {1} channel(s)")
            fun data(): Collection<Array<Any>> {
                return listOf(arrayOf(4000, 1), arrayOf(24000, 4), arrayOf(44000, 6))
            }
        }

        @Test(expected = DecoderException::class)
        fun test() {
            OpusAudioDecoder(rate, channels)
        }
    }
}
