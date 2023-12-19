package com.fake.soundremote.util

import com.fake.soundremote.createKeystrokeWithMods
import com.fake.soundremote.getKeystroke
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("KeyUtils")
internal class KeysTest {

    @DisplayName("Char.toKeyCode")
    @Nested
    inner class ToKeyCodeTests {
        @ParameterizedTest
        @DisplayName("returns correct code for a digit or [a-z/A-Z] letter Char")
        @CsvSource("0, 48", "9, 57", "a, 65", "A, 65", "z, 90", "Z, 90")
        fun validChar_returnsCorrectCode(ch: Char, expected: Int) {
            val actual = ch.toKeyCode()

            assertEquals(KeyCode(expected), actual)
        }

        @ParameterizedTest
        @DisplayName("returns null for a non digit or [a-z/A-Z] letter Char")
        @ValueSource(chars = ['%', 'Ж', '©'])
        fun invalidChar_returnsNull(ch: Char) {
            val actual = ch.toKeyCode()

            assertNull(actual)
        }
    }

    @DisplayName("Int.toLetterOrDigitChar")
    @Nested
    inner class KeyCodeToCharTests {
        @ParameterizedTest
        @DisplayName("returns correct Char for a digit or [a-z/A-Z] letter key code")
        @CsvSource("48, 0", "57, 9", "65, a", "90, z")
        fun validCode_returnsCorrectChar(code: Int, expected: Char) {
            val actual = KeyCode(code).toLetterOrDigitChar()

            assertEquals(expected, actual)
        }

        @ParameterizedTest
        @DisplayName("returns null for a non digit or [a-z/A-Z] letter key code")
        @ValueSource(ints = [-1, 0, 200, 500])
        fun invalidCode_returnsNull(code: Int) {
            val actual = KeyCode(code).toLetterOrDigitChar()

            assertNull(actual)
        }
    }

    @DisplayName("generateDescription")
    @Nested
    inner class GenerateDescriptionTests {
        @ParameterizedTest
        @DisplayName("produces description without mod labels for a keystroke without mods")
        @EnumSource(ModKey::class)
        fun keystrokeWithoutMods_ContainsNoModLabel(mod: ModKey) {
            val keystroke = createKeystrokeWithMods(null)

            val description = generateDescription(keystroke)

            val actual = description.lowercase().contains(mod.label.lowercase())
            assertFalse(actual)
        }

        @ParameterizedTest
        @DisplayName("produces description with a mod label for a keystroke with mod")
        @EnumSource(ModKey::class)
        fun keystrokeWithOneMod_ContainsCorrectModLabel(mod: ModKey) {
            val keystroke = createKeystrokeWithMods(mod.bitField)

            val description = generateDescription(keystroke)

            val actual = description.lowercase().contains(mod.label.lowercase())
            assertTrue(actual)
        }

        @ParameterizedTest
        @DisplayName("produces description with a correct label for a passed key code")
        @CsvSource("45, insert", "121, f10", "48, 0", "57, 9", "65, a", "90, Z")
        fun keystroke_ContainsCorrectKeyLabel(code: Int, label: String) {
            val keystroke = getKeystroke(keyCode = KeyCode(code))

            val description = generateDescription(keystroke)

            val actual = description.lowercase().contains(label.lowercase())
            assertTrue(actual)
        }
    }
}
