package com.fake.soundremote.ui

import androidx.lifecycle.SavedStateHandle
import com.fake.soundremote.MainDispatcherExtension
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.TestKeystrokeRepository
import com.fake.soundremote.ui.keystroke.KEYSTROKE_ID_ARG
import com.fake.soundremote.ui.keystroke.KeystrokeScreenMode
import com.fake.soundremote.ui.keystroke.KeystrokeViewModel
import com.fake.soundremote.util.KeyGroup
import com.fake.soundremote.util.ModKey
import com.fake.soundremote.util.createMods
import com.fake.soundremote.util.isModActive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExtendWith(MainDispatcherExtension::class)
@DisplayName("KeystrokeViewModel")
class KeystrokeViewModelTest {
    private var keystrokeRepository = TestKeystrokeRepository()

    private lateinit var viewModel: KeystrokeViewModel

    @Nested
    @DisplayName("Create Keystroke mode")
    inner class CreateKeystroke {
        @BeforeEach
        fun setup() {
            val savedState = SavedStateHandle(emptyMap())
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)
        }

        @Test
        @DisplayName("Sets screen mode correctly")
        fun screenMode_isCorrect() {
            val actual = viewModel.keystrokeScreenState.value.mode

            assertEquals(KeystrokeScreenMode.CREATE, actual)
        }
    }

    @Nested
    @DisplayName("Edit Keystroke mode")
    inner class EditKeystroke {
        @Test
        @DisplayName("Sets screen mode correctly")
        fun screenMode_isCorrect() {
            val id = 1
            val keystroke = Keystroke(id, 123, 0, "K", true, 0)
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)

            val actual = viewModel.keystrokeScreenState.value.mode

            assertEquals(KeystrokeScreenMode.EDIT, actual)
        }

        @Test
        @DisplayName("Sets keystroke properties correctly")
        fun keystrokeProperties_areCorrect() {
            val mods = createMods(win = true, ctrl = true, shift = true, alt = true)
            val id = 1
            val keystroke = Keystroke(id, 100, mods, "TestK", false, 0)
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)

            val state = viewModel.keystrokeScreenState.value

            assertEquals(keystroke.name, state.name)
            assertEquals(keystroke.keyCode, state.keyCode)
            assertEquals(keystroke.isModActive(ModKey.ALT), state.alt)
            assertEquals(keystroke.isModActive(ModKey.CTRL), state.ctrl)
            assertEquals(keystroke.isModActive(ModKey.SHIFT), state.shift)
            assertEquals(keystroke.isModActive(ModKey.WIN), state.win)
        }

        @ParameterizedTest
        @MethodSource("com.fake.soundremote.ui.KeystrokeViewModelTest#keyCodeToGroup")
        @DisplayName("Sets key group correctly")
        fun keyGroup_isCorrect(keyCode: Int, expectedKeyGroup: KeyGroup) {
            val id = 1
            val keystroke = Keystroke(id, keyCode, 0, "K", true, 0)
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)

            val actual = viewModel.keystrokeScreenState.value.keyGroupIndex

            assertEquals(expectedKeyGroup.index, actual)
        }
    }

    companion object {
        @JvmStatic
        private fun keyCodeToGroup(): Stream<Arguments> {
            return Stream.of(
                arguments(0x30, KeyGroup.LETTER_DIGIT),
                arguments(0x39, KeyGroup.LETTER_DIGIT),
                arguments(0x41, KeyGroup.LETTER_DIGIT),
                arguments(0x5A, KeyGroup.LETTER_DIGIT),
                arguments(0xAD, KeyGroup.MEDIA),
                arguments(0xB3, KeyGroup.MEDIA),
                arguments(0xBA, KeyGroup.TYPING),
                arguments(0xDC, KeyGroup.TYPING),
                arguments(0x09, KeyGroup.CONTROL),
                arguments(0x91, KeyGroup.CONTROL),
                arguments(0x21, KeyGroup.NAVIGATION),
                arguments(0x2E, KeyGroup.NAVIGATION),
                arguments(0x60, KeyGroup.NUM_PAD),
                arguments(0x90, KeyGroup.NUM_PAD),
                arguments(0x70, KeyGroup.FUNCTION),
                arguments(0x7B, KeyGroup.FUNCTION),
            )
        }
    }
}
