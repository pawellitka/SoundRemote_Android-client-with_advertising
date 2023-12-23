package com.fake.soundremote.ui.keystroke

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.util.KeyCode
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