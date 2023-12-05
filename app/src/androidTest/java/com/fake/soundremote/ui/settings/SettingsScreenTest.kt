package com.fake.soundremote.ui.settings

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Rule
import org.junit.Test

internal class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)

    // Settings screen should contain navigate up arrow
    @Test
    fun navigateUp_exist() {
        composeTestRule.setContent {
            CreateSettingsScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertIsDisplayed()
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun CreateSettingsScreen(
        modifier: Modifier = Modifier,
        settings: SettingsUIState = SettingsUIState(),
        onSetServerPort: (Int) -> Unit = {},
        onSetClientPort: (Int) -> Unit = {},
        onSetAudioCompression: (Int) -> Unit = {},
        onNavigateUp: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            SettingsScreen(
                settings = settings,
                onSetServerPort = onSetServerPort,
                onSetClientPort = onSetClientPort,
                onSetAudioCompression = onSetAudioCompression,
                onNavigateUp = onNavigateUp,
                modifier = modifier,
            )
        }
    }
}
