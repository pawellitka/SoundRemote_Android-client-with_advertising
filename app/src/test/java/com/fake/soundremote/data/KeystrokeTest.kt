package com.fake.soundremote.data

import com.fake.soundremote.createKeystrokeWithMods
import com.fake.soundremote.util.ModKey
import com.fake.soundremote.util.isModActive
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@DisplayName("Keystroke")
internal class KeystrokeTest {

    @DisplayName("Mods")
    @Nested
    inner class ModsTests {
        @DisplayName("Are not set when created without mods")
        @ParameterizedTest
        @EnumSource(ModKey::class)
        fun isModActive_NoMods_ReturnsFalse(mod: ModKey) {
            val keystroke = createKeystrokeWithMods(null)

            val modActive = keystroke.isModActive(mod)

            assertFalse(modActive)
        }

        @DisplayName("Are set when created with mods")
        @ParameterizedTest
        @EnumSource(ModKey::class)
        fun isModActive_WithMods_ReturnsTrue(mod: ModKey) {
            val keystroke = createKeystrokeWithMods(mod.bitField)

            val modActive = keystroke.isModActive(mod)

            assertTrue(modActive)
        }

        @DisplayName("Bitfield is correct set when created without mods")
        @Test
        fun modsBitfield_NoMods_ReturnsCorrectValue() {
            val expected = 0
            val keystroke = createKeystrokeWithMods(null)

            val actual = keystroke.mods

            assertEquals(expected, actual)
        }

        @DisplayName("Bitfield is correct set when created with Mods")
        @ParameterizedTest
        @EnumSource(ModKey::class)
        fun modsBitfield_WithMod_ReturnsCorrectValue(mod: ModKey) {
            val expected = mod.bitField
            val keystroke = createKeystrokeWithMods(mod.bitField)

            val actual = keystroke.mods

            assertEquals(expected, actual)
        }
    }
}
