package com.fake.soundremote.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fake.soundremote.R
import com.fake.soundremote.data.Action
import com.fake.soundremote.data.ActionType
import com.fake.soundremote.data.AppAction
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ActionSelectDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val ok by composeTestRule.stringResource(android.R.string.ok)
    private val cancel by composeTestRule.stringResource(R.string.cancel)
    private val noAction by composeTestRule.stringResource(R.string.action_none)
    private val appActionType by composeTestRule.stringResource(ActionType.APP.nameStringId)
    private val keystrokeActionType by composeTestRule.stringResource(ActionType.KEYSTROKE.nameStringId)
    private val allActionTypes by lazy { setOf(appActionType, keystrokeActionType) }

    @Test
    fun cancelButton_onClick_dismisses() {
        var dismissed = false
        composeTestRule.setContent {
            CreateActionSelectDialog(onDismiss = { dismissed = true })
        }

        composeTestRule.onNodeWithText(cancel).performClick()

        Assert.assertTrue(dismissed)
    }

    // When all action types are available, all action types are displayed
    @Test
    fun allActionTypesAvailable_allActionTypesAreDisplayed() {
        composeTestRule.setContent {
            CreateActionSelectDialog(availableActionTypes = ActionType.entries.toSet())
        }

        for (actionType in allActionTypes) {
            composeTestRule.onNodeWithText(actionType).assertIsDisplayed()
        }
    }

    // When only one action type is available, other action types don't exist
    @Test
    fun singleActionTypeAvailable_singleAppActionTypeIsDisplayed() {
        composeTestRule.setContent {
            CreateActionSelectDialog(availableActionTypes = setOf(ActionType.APP))
        }

        composeTestRule.onNodeWithText(appActionType).assertIsDisplayed()
        composeTestRule.onNodeWithText(keystrokeActionType).assertDoesNotExist()
    }

    // When initial action is null, `No action` option must stay selected on action type change
    @Test
    fun initialActionIsNull_actionTypeChange_noActionSelected() {
        composeTestRule.setContent {
            CreateActionSelectDialog(
                availableActionTypes = ActionType.entries.toSet(),
                initialAction = null,
            )
        }

        composeTestRule.onNodeWithText(noAction).assertIsSelected()
        for (actionType in allActionTypes) {
            composeTestRule.onNodeWithText(actionType).performClick()
            composeTestRule.onNodeWithText(noAction).assertIsSelected()
        }
    }

    // All actions exist for app action type
    @Test
    fun appActionType_allActions_areDisplayed() {
        composeTestRule.setContent {
            CreateActionSelectDialog(
                availableActionTypes = setOf(ActionType.APP),
            )
        }

        for (appAction in AppAction.entries) {
            val name = composeTestRule.activity.getString(appAction.nameStringId)
            composeTestRule.onNodeWithText(name).assertExists()
        }
    }

    // Given: a long list of Keystrokes that doesn't fit into screen and initially selected
    // keystroke id at the end of a that list.
    // Expected: dialog should scroll to the selected Keystroke.
    @Test
    fun initialActionIsKeystroke_needsScrolling_isDisplayed() {
        val count = 100
        val keystrokes = buildList {
            repeat(count) {
                val id = it + 1
                add(KeystrokeInfoUIState(id, "Key $id", "Desc $id"))
            }
        }
        composeTestRule.setContent {
            CreateActionSelectDialog(
                availableActionTypes = ActionType.entries.toSet(),
                initialAction = Action(ActionType.KEYSTROKE, count),
                keystrokes = keystrokes,
            )
        }

        composeTestRule.onNodeWithText("Key $count").apply {
            assertIsDisplayed()
            assertIsSelected()
        }
    }

    @Test
    fun okButton_onClick_confirmsWithCorrectAction() {
        val count = 5
        val expected = Action(ActionType.KEYSTROKE, 1)
        var actual: Action? = null
        composeTestRule.setContent {
            val keystrokes = buildList {
                repeat(count) {
                    val id = it + 1
                    add(KeystrokeInfoUIState(id, "Key $id", "Desc $id"))
                }
            }
            CreateActionSelectDialog(
                availableActionTypes = ActionType.entries.toSet(),
                initialAction = Action(ActionType.APP, AppAction.DISCONNECT.id),
                keystrokes = keystrokes,
                onConfirm = { actual = it },
            )
        }

        composeTestRule.apply {
            onNodeWithText(keystrokeActionType).performClick()
            onNodeWithText("Key 1").performClick()
            onNodeWithText(ok).performClick()
        }

        Assert.assertEquals(expected, actual)
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun CreateActionSelectDialog(
        availableActionTypes: Set<ActionType> = ActionType.entries.toSet(),
        initialAction: Action? = null,
        keystrokes: List<KeystrokeInfoUIState> = emptyList(),
        onConfirm: (Action?) -> Unit = {},
        onDismiss: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            ActionSelectDialog(
                availableActionTypes = availableActionTypes,
                initialAction = initialAction,
                keystrokes = keystrokes,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
            )
        }
    }
}
