package com.fake.soundremote.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)

    // Home screen should not contain navigate up arrow
    @Test
    fun homeScreen_noNavigateUp() {
        composeTestRule.setContent {
            val uiState = HomeUIState()
            SoundRemoteTheme {
                HomeScreen(
                    uiState = uiState,
                    messageId = null,
                    onSendKeystroke = {},
                    onEditKeystroke = {},
                    onConnect = {},
                    onDisconnect = {},
                    onSetMuted = {},
                    onMessageShown = {},
                    onNavigateToEvents = {},
                    onNavigateToSettings = {},
                    onNavigateToAbout = {},
                    showSnackbar = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertDoesNotExist()
    }
}
