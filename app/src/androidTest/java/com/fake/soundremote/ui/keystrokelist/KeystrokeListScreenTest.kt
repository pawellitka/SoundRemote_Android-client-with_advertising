package com.fake.soundremote.ui.keystrokelist

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.util.KeystrokeDescription
import com.fake.soundremote.util.TestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

internal class KeystrokeListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val navigateUp by composeTestRule.stringResource(R.string.navigate_up)
    private val keystrokeActionsMenu by composeTestRule.stringResource(R.string.keystroke_actions_menu_description)
    private val actionEdit by composeTestRule.stringResource(R.string.edit)
    private val actionDelete by composeTestRule.stringResource(R.string.delete)
    private val deleteConfirmationTemplate by composeTestRule.stringResource(R.string.keystroke_delete_confirmation)

    // Keystroke list screen should contain navigate up arrow
    @Test
    fun navigateUp_exist() {
        composeTestRule.setContent {
            CreateKeystrokeListScreen()
        }

        composeTestRule.onNodeWithContentDescription(navigateUp).assertIsDisplayed()
    }

    // Keystroke name and description are displayed
    @Test
    fun keystroke_isDisplayed() {
        val name = "Test name"
        val desc = "Test description"
        val keystrokeState = KeystrokeUIState(1, name, KeystrokeDescription.WithString(desc), false)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(state = KeystrokeListUIState(listOf(keystrokeState)))
        }

        composeTestRule.onNodeWithText(name).assertIsDisplayed()
        composeTestRule.onNodeWithText(desc).assertIsDisplayed()
    }

    // Keystroke favoured status switch is displayed
    @Test
    fun favouredSwitch_isDisplayed() {
        val name = "Test name"
        val desc = KeystrokeDescription.WithString("Test description")
        val keystrokeState = KeystrokeUIState(1, name, desc, true)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(state = KeystrokeListUIState(listOf(keystrokeState)))
        }

        composeTestRule.onNodeWithTag(TestTags.FAVOURITE_SWITCH).assertIsDisplayed()
    }

    // Keystroke favoured status switch is on for favoured keystroke
    @Test
    fun favouredSwitch_keystrokeIsFavoured_isOn() {
        val name = "Test name"
        val desc = KeystrokeDescription.WithString("Test description")
        val keystrokeState = KeystrokeUIState(1, name, desc, true)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(state = KeystrokeListUIState(listOf(keystrokeState)))
        }

        composeTestRule.onNodeWithTag(TestTags.FAVOURITE_SWITCH).assertIsOn()
    }

    // Keystroke favoured status switch is off for unfavoured keystroke
    @Test
    fun favouredSwitch_keystrokeIsNotFavoured_isOff() {
        val name = "Test name"
        val desc = KeystrokeDescription.WithString("Test description")
        val keystrokeState = KeystrokeUIState(1, name, desc, false)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(state = KeystrokeListUIState(listOf(keystrokeState)))
        }

        composeTestRule.onNodeWithTag(TestTags.FAVOURITE_SWITCH).assertIsOff()
    }

    // Keystroke actions menu is displayed on actions menu button click
    @Test
    fun keystrokeActionsButton_onClick_displaysMenu() {
        val desc = KeystrokeDescription.WithString("Desc")
        val keystrokeState = KeystrokeUIState(1, "Name", desc, false)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(state = KeystrokeListUIState(listOf(keystrokeState)))
        }

        composeTestRule.apply {
            onNodeWithContentDescription(keystrokeActionsMenu).performClick()
            onNodeWithText(actionEdit).assertIsDisplayed()
            onNodeWithText(actionDelete).assertIsDisplayed()
        }
    }

    // Keystroke edit action calls "onEdit" with correct id
    @Test
    fun keystrokeActionEdit_onClick_callsEdit() {
        val id = 42
        val desc = KeystrokeDescription.WithString("Desc")
        val keystrokeState = KeystrokeUIState(id, "Name", desc, false)
        var actualId = -1
        composeTestRule.setContent {
            CreateKeystrokeListScreen(
                state = KeystrokeListUIState(listOf(keystrokeState)),
                onEdit = { actualId = id },
            )
        }

        composeTestRule.onNodeWithContentDescription(keystrokeActionsMenu).performClick()
        composeTestRule.onNodeWithText(actionEdit).performClick()

        assertEquals(id, actualId)
    }

    // Keystroke delete action shows confirmation dialog
    @Test
    fun keystrokeActionDelete_onClick_showsConfirmationDialog() {
        val name = "Test name"
        val desc = KeystrokeDescription.WithString("Desc")
        val keystrokeState = KeystrokeUIState(1, name, desc, false)
        composeTestRule.setContent {
            CreateKeystrokeListScreen(
                state = KeystrokeListUIState(listOf(keystrokeState)),
            )
        }

        composeTestRule.apply {
            onNodeWithContentDescription(keystrokeActionsMenu).performClick()
            onNodeWithText(actionDelete).performClick()

            val confirmationText = deleteConfirmationTemplate.format(name)
            onNodeWithText(confirmationText).assertIsDisplayed()
        }
    }

    // Clicking Delete button in delete confirmation dialog calls "onDelete"
    @Test
    fun deleteConfirmationDialog_onClickDelete_callsDelete() {
        val id = 42
        val desc = KeystrokeDescription.WithString("Desc")
        val keystrokeState = KeystrokeUIState(id, "Test name", desc, false)
        var actualId = -1
        composeTestRule.setContent {
            CreateKeystrokeListScreen(
                state = KeystrokeListUIState(listOf(keystrokeState)),
                onDelete = { actualId = id },
            )
        }

        composeTestRule.apply {
            onNodeWithContentDescription(keystrokeActionsMenu).performClick()
            // Menu - Delete
            onNodeWithText(actionDelete).performClick()
            // Confirmation dialog - Delete button
            onNodeWithText(actionDelete).performClick()
        }

        assertEquals(id, actualId)
    }

    // Click on keystroke calls "onEdit" function with correct id
    @Test
    fun keystroke_onClick_callsEdit() {
        val id = 42
        val name = "Test name"
        val desc = KeystrokeDescription.WithString("Desc")
        val keystrokeState = KeystrokeUIState(id, name, desc, false)
        var actualId = -1
        composeTestRule.setContent {
            CreateKeystrokeListScreen(
                state = KeystrokeListUIState(listOf(keystrokeState)),
                onEdit = { actualId = id },
            )
        }

        composeTestRule.onNodeWithText(name).performClick()

        assertEquals(id, actualId)
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun CreateKeystrokeListScreen(
        modifier: Modifier = Modifier,
        state: KeystrokeListUIState = KeystrokeListUIState(),
        onCreate: () -> Unit = {},
        onEdit: (id: Int) -> Unit = {},
        onDelete: (id: Int) -> Unit = {},
        onChangeFavoured: (id: Int, favoured: Boolean) -> Unit = { _, _ -> },
        onMove: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
        onNavigateUp: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            KeystrokeListScreen(
                state,
                onNavigateToKeystrokeCreate = onCreate,
                onNavigateToKeystrokeEdit = onEdit,
                onDelete = onDelete,
                onChangeFavoured = onChangeFavoured,
                onMove = onMove,
                onNavigateUp = onNavigateUp,
                modifier = modifier,
            )
        }
    }
}
