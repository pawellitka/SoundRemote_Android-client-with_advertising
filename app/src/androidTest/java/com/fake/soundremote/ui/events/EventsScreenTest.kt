package com.fake.soundremote.ui.events

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

internal class EventsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)

    // Events screen should contain navigate up arrow
    @Test
    fun navigateUp_exist() {
        composeTestRule.setContent {
            CreateEventsScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertIsDisplayed()
    }

    @Composable
    private fun CreateEventsScreen(
        modifier: Modifier = Modifier,
        eventsUIState: EventsUIState = EventsUIState(),
        onSetKeystrokeForEvent: (eventId: Int, keystrokeId: Int?) -> Unit = { _, _ -> },
        onNavigateUp: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            EventsScreen(
                eventsUIState = eventsUIState,
                onSetKeystrokeForEvent = onSetKeystrokeForEvent,
                onNavigateUp = onNavigateUp,
                modifier = modifier,
            )
        }
    }
}
