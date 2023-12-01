package com.fake.soundremote.ui.keystrokelist

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

internal class KeystrokeListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)

    // Keystroke list screen should contain navigate up arrow
    @Test
    fun navigateUp_exist() {
        composeTestRule.setContent {
            CreateKeystrokeListScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertIsDisplayed()
    }

    @Composable
    private fun CreateKeystrokeListScreen(
        modifier: Modifier = Modifier,
        state: KeystrokeListUIState = KeystrokeListUIState(),
        onCreate: () -> Unit = {},
        onEdit: (id: Int) -> Unit = {},
        onDelete: (id: Int) -> Unit = {},
        onChangeFavoured: (id: Int, favoured: Boolean) -> Unit  = { _, _ -> },
        onMove: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
        onNavigateUp: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            KeystrokeListScreen(
                state,
                onCreate = onCreate,
                onEdit = onEdit,
                onDelete = onDelete,
                onChangeFavoured = onChangeFavoured,
                onMove = onMove,
                onNavigateUp = onNavigateUp,
                modifier = modifier,
            )
        }
    }
}
