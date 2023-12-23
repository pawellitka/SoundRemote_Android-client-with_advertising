package com.fake.soundremote.ui.keystroke

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.util.KeyCode
import com.fake.soundremote.util.ModKey
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

internal class KeystrokeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)

    // Keystroke screen should contain navigate up arrow
    @Test
    fun navigateUp_exist() {
        composeTestRule.setContent {
            CreateKeystrokeScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertIsDisplayed()
    }

    // Mods
    @Test
    fun winMod_click_changesWinMod() {
        val initial = true
        val expected = !initial
        var actual: Boolean? = null
        composeTestRule.setContent {
            CreateKeystrokeScreen(
                state = KeystrokeScreenUIState(win = initial),
                onWinChange = { actual = it },
            )
        }

        composeTestRule.onNodeWithText(ModKey.WIN.label).performClick()

        assertEquals(expected, actual)
    }

    @Test
    fun ctrlMod_click_changesCtrlMod() {
        val initial = true
        val expected = !initial
        var actual: Boolean? = null
        composeTestRule.setContent {
            CreateKeystrokeScreen(
                state = KeystrokeScreenUIState(ctrl = initial),
                onCtrlChange = { actual = it },
            )
        }

        composeTestRule.onNodeWithText(ModKey.CTRL.label).performClick()

        assertEquals(expected, actual)
    }

    @Test
    fun shiftMod_click_changesShiftMod() {
        val initial = true
        val expected = !initial
        var actual: Boolean? = null
        composeTestRule.setContent {
            CreateKeystrokeScreen(
                state = KeystrokeScreenUIState(shift = initial),
                onShiftChange = { actual = it },
            )
        }

        composeTestRule.onNodeWithText(ModKey.SHIFT.label).performClick()

        assertEquals(expected, actual)
    }

    @Test
    fun altMod_click_changesAltMod() {
        val initial = true
        val expected = !initial
        var actual: Boolean? = null
        composeTestRule.setContent {
            CreateKeystrokeScreen(
                state = KeystrokeScreenUIState(alt = initial),
                onAltChange = { actual = it },
            )
        }

        composeTestRule.onNodeWithText(ModKey.ALT.label).performClick()

        assertEquals(expected, actual)
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun CreateKeystrokeScreen(
        modifier: Modifier = Modifier,
        state: KeystrokeScreenUIState = KeystrokeScreenUIState(),
        onKeyCodeChange: (KeyCode?) -> Unit = {},
        onWinChange: (Boolean) -> Unit = {},
        onCtrlChange: (Boolean) -> Unit = {},
        onShiftChange: (Boolean) -> Unit = {},
        onAltChange: (Boolean) -> Unit = {},
        onNameChange: (String) -> Unit = {},
        checkCanSave: () -> Boolean = { false },
        onSave: () -> Unit = {},
        onClose: () -> Unit = {},
        showSnackbar: (String, SnackbarDuration) -> Unit = { _, _ -> },
        compactHeight: Boolean = false,
    ) {
        SoundRemoteTheme {
            KeystrokeScreen(
                state,
                onKeyCodeChange,
                onWinChange,
                onCtrlChange,
                onShiftChange,
                onAltChange,
                onNameChange,
                checkCanSave,
                onSave,
                onClose,
                showSnackbar,
                compactHeight,
                modifier,
            )
        }
    }
}