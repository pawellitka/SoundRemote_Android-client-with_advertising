package com.fake.soundremote.ui.home

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isOff
import androidx.compose.ui.test.isOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

internal class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)
    private val mute by composeTestRule.stringResource(R.string.mute)
    private val unmute by composeTestRule.stringResource(R.string.unmute)

    // Home screen should not contain navigate up arrow
    @Test
    fun navigateUp_doesNotExist() {
        composeTestRule.setContent {
            CreateHomeScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertDoesNotExist()
    }

    // Mute button is not toggled when muted state is off
    @Test
    fun muteButton_stateNotMuted_notToggled() {
        val uiState = HomeUIState(isMuted = false)
        composeTestRule.setContent {
            CreateHomeScreen(uiState = uiState)
        }

        composeTestRule.onNodeWithContentDescription(mute).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(mute).assert(isOff())
    }

    // Mute button is toggled when muted state is on
    @Test
    fun muteButton_stateMuted_toggled() {
        val uiState = HomeUIState(isMuted = true)
        composeTestRule.setContent {
            CreateHomeScreen(uiState = uiState)
        }

        composeTestRule.onNodeWithContentDescription(unmute).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(unmute).assert(isOn())
    }

    // Click on mute button mutes
    @Test
    fun muteButton_click_mutes() {
        var actual = false
        composeTestRule.setContent {
            val uiState = HomeUIState(isMuted = false)
            CreateHomeScreen(uiState = uiState, onSetMuted = { actual = it })
        }

        composeTestRule.onNodeWithContentDescription(mute).performClick()

        assertTrue(actual)
    }

    // Click on unmute button unmutes
    @Test
    fun unmuteButton_click_unmutes() {
        var actual = true
        composeTestRule.setContent {
            val uiState = HomeUIState(isMuted = true)
            CreateHomeScreen(uiState = uiState, onSetMuted = { actual = it })
        }

        composeTestRule.onNodeWithContentDescription(unmute).performClick()

        assertFalse(actual)
    }

    @Composable
    private fun CreateHomeScreen(
        modifier: Modifier = Modifier,
        uiState: HomeUIState = HomeUIState(),
        @StringRes messageId: Int? = null,
        onSendKeystroke: (Int) -> Unit = {},
        onEditKeystroke: (Int) -> Unit = {},
        onConnect: (String) -> Unit = {},
        onDisconnect: () -> Unit = {},
        onSetMuted: (Boolean) -> Unit = {},
        onMessageShown: () -> Unit = {},
        onNavigateToEvents: () -> Unit = {},
        onNavigateToSettings: () -> Unit = {},
        onNavigateToAbout: () -> Unit = {},
        showSnackbar: (String, SnackbarDuration) -> Unit = { _, _ -> },
    ) {
        SoundRemoteTheme {
            HomeScreen(
                uiState = uiState,
                messageId = messageId,
                onSendKeystroke = onSendKeystroke,
                onEditKeystroke = onEditKeystroke,
                onConnect = onConnect,
                onDisconnect = onDisconnect,
                onSetMuted = onSetMuted,
                onMessageShown = onMessageShown,
                onNavigateToEvents = onNavigateToEvents,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToAbout = onNavigateToAbout,
                showSnackbar = showSnackbar,
                modifier = modifier,
            )
        }
    }
}
