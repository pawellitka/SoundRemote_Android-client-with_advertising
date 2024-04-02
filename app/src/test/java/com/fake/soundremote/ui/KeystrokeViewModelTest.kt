package com.fake.soundremote.ui

import androidx.lifecycle.SavedStateHandle
import com.fake.soundremote.MainDispatcherExtension
import com.fake.soundremote.data.TestKeystrokeRepository
import com.fake.soundremote.getKeystroke
import com.fake.soundremote.ui.keystroke.KEYSTROKE_ID_ARG
import com.fake.soundremote.ui.keystroke.KeystrokeScreenMode
import com.fake.soundremote.ui.keystroke.KeystrokeViewModel
import com.fake.soundremote.util.KeyCode
import com.fake.soundremote.util.KeyGroup
import com.fake.soundremote.util.ModKey
import com.fake.soundremote.util.Mods
import com.fake.soundremote.util.generateDescription
import com.fake.soundremote.util.isModActive
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.EnumSource
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

        @Test
        @DisplayName("updateKeyCode() updates keyCode")
        fun updateKeyCode_updates() {
            val expected = KeyCode(0x60)
            assertNotEquals(expected, viewModel.keystrokeScreenState.value.keyCode)

            viewModel.updateKeyCode(expected)

            val actual = viewModel.keystrokeScreenState.value.keyCode
            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("updateName() updates keystroke name")
        fun updateName_updates() {
            val expected = "KeystrokeName"
            assertNotEquals(expected, viewModel.keystrokeScreenState.value.name)

            viewModel.updateName(expected)

            val actual = viewModel.keystrokeScreenState.value.name
            assertEquals(expected, actual)
        }

        @ParameterizedTest
        @EnumSource(ModKey::class)
        @DisplayName("updateMod() updates keystroke mod")
        fun updateMod_updates(mod: ModKey) {
            val modState = when (mod) {
                ModKey.WIN -> viewModel.keystrokeScreenState.value.win
                ModKey.CTRL -> viewModel.keystrokeScreenState.value.ctrl
                ModKey.SHIFT -> viewModel.keystrokeScreenState.value.shift
                ModKey.ALT -> viewModel.keystrokeScreenState.value.alt
            }
            assertFalse(modState)

            viewModel.updateMod(mod, true)

            val actual = when (mod) {
                ModKey.WIN -> viewModel.keystrokeScreenState.value.win
                ModKey.CTRL -> viewModel.keystrokeScreenState.value.ctrl
                ModKey.SHIFT -> viewModel.keystrokeScreenState.value.shift
                ModKey.ALT -> viewModel.keystrokeScreenState.value.alt
            }
            assertTrue(actual)
        }

        @Test
        @DisplayName("canSave() returns false if keyCode is null")
        fun canSave_keyCodeIsNull_returnsFalse() {
            assertNull(viewModel.keystrokeScreenState.value.keyCode)
            assertFalse(viewModel.canSave())
        }

        @Test
        @DisplayName("saveKeystroke() saves new Keystroke")
        fun saveKeystroke_createsNewKeystroke() = runTest {
            val expectedName = "TestName12"
            val expectedKeyCode = KeyCode(0x42)
            viewModel.updateName(expectedName)
            viewModel.updateKeyCode(expectedKeyCode)
            viewModel.updateMod(ModKey.SHIFT, true)
            viewModel.updateMod(ModKey.CTRL, true)
            keystrokeRepository.setKeystrokes(emptyList())

            viewModel.saveKeystroke("B")

            val savedKeystroke = keystrokeRepository.getAllOrdered().firstOrNull()?.firstOrNull()
            assertNotNull(savedKeystroke)
            savedKeystroke!!
            assertEquals(expectedName, savedKeystroke.name)
            assertEquals(expectedKeyCode, savedKeystroke.keyCode)
            assertTrue(savedKeystroke.isModActive(ModKey.CTRL))
            assertTrue(savedKeystroke.isModActive(ModKey.SHIFT))
            assertFalse(savedKeystroke.isModActive(ModKey.ALT))
            assertFalse(savedKeystroke.isModActive(ModKey.WIN))
        }

        @Test
        @DisplayName("saveKeystroke() without name set saves new Keystroke with generated name")
        fun saveKeystroke_blankName_createsNewKeystroke() = runTest {
            // Letter/digit key
            val expectedKeyCode = KeyCode(0x42)
            val keyLabel = expectedKeyCode.toLetterOrDigitString()!!
            val mods = Mods(win = false, ctrl = true, shift = true, alt = false)
            val expectedName = generateDescription(keyLabel, mods)

            for (mod in ModKey.entries) {
                viewModel.updateMod(mod, mods.isModActive(mod))
            }
            viewModel.updateKeyCode(expectedKeyCode)
            keystrokeRepository.setKeystrokes(emptyList())

            viewModel.saveKeystroke(keyLabel)

            val savedKeystroke = keystrokeRepository.getAllOrdered().firstOrNull()?.firstOrNull()
            assertNotNull(savedKeystroke)
            savedKeystroke!!
            assertEquals(expectedName, savedKeystroke.name)
            assertEquals(expectedKeyCode, savedKeystroke.keyCode)
            for (mod in ModKey.entries) {
                val expectedModValue = mods.isModActive(mod)
                val actualModValue = savedKeystroke.isModActive(mod)
                assertEquals(expectedModValue, actualModValue)
            }
        }
    }

    @Nested
    @DisplayName("Edit Keystroke mode")
    inner class EditKeystroke {
        @Test
        @DisplayName("Sets screen mode correctly")
        fun screenMode_isCorrect() {
            val id = 1
            val keystroke = getKeystroke(id = id)
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)

            val actual = viewModel.keystrokeScreenState.value.mode

            assertEquals(KeystrokeScreenMode.EDIT, actual)
        }

        @Test
        @DisplayName("Sets keystroke properties correctly")
        fun keystrokeProperties_areCorrect() {
            val mods = Mods(win = true, ctrl = true, shift = true, alt = true)
            val id = 1
            val keystroke = getKeystroke(id = id, mods = mods)
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
            val keystroke = getKeystroke(id = id, keyCode = KeyCode(keyCode))
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)

            val actual = viewModel.keystrokeScreenState.value.keyGroupIndex

            assertEquals(expectedKeyGroup.index, actual)
        }

        @Test
        @DisplayName("saveKeystroke() updates Keystroke")
        fun saveKeystroke_updatesKeystroke() = runTest {
            // Create a Keystroke to edit
            val id = 10
            val keystroke = getKeystroke(
                id = id,
                keyCode = KeyCode(0x100),
                mods = Mods(),
                name = "Original name"
            )
            keystrokeRepository.setKeystrokes(listOf(keystroke))
            val savedState = SavedStateHandle(mapOf(KEYSTROKE_ID_ARG to id))
            viewModel = KeystrokeViewModel(savedState, keystrokeRepository)
            val expectedName = "New name"
            val expectedKeyCode = KeyCode(0x42)

            viewModel.updateName(expectedName)
            viewModel.updateKeyCode(expectedKeyCode)
            viewModel.updateMod(ModKey.SHIFT, true)
            viewModel.updateMod(ModKey.CTRL, true)
            viewModel.saveKeystroke("B")

            val updatedKeystroke = keystrokeRepository.getById(id)
            assertNotNull(updatedKeystroke)
            updatedKeystroke!!
            assertEquals(expectedName, updatedKeystroke.name)
            assertEquals(expectedKeyCode, updatedKeystroke.keyCode)
            assertTrue(updatedKeystroke.isModActive(ModKey.CTRL))
            assertTrue(updatedKeystroke.isModActive(ModKey.SHIFT))
            assertFalse(updatedKeystroke.isModActive(ModKey.ALT))
            assertFalse(updatedKeystroke.isModActive(ModKey.WIN))
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
