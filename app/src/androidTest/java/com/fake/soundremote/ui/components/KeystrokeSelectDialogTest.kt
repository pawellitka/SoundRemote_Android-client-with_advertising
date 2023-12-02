package com.fake.soundremote.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.keystrokelist.KeystrokeUIState
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class KeystrokeSelectDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val dialogTitle by composeTestRule.stringResource(R.string.keystroke_select_title)
    private val noKeystroke by composeTestRule.stringResource(R.string.keystroke_select_none)
    private val ok by composeTestRule.stringResource(android.R.string.ok)
    private val cancel by composeTestRule.stringResource(R.string.cancel)

    // "No keystroke" option should be displayed
    @Test
    fun noKeystrokeOption_isDisplayed() {
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog()
        }

        composeTestRule.onNodeWithText(noKeystroke).assertIsDisplayed()
    }

    // "No keystroke" option should be selected if there are no other options
    @Test
    fun noKeystrokeOption_noKeystrokes_isSelected() {
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog()
        }

        composeTestRule.onNodeWithText(noKeystroke).assertIsSelected()
    }

    // "No keystroke" option should be selected when `initialKeystrokeId` argument is null
    @Test
    fun noKeystrokeOption_initialIdIsNull_isSelected() {
        val items = buildList {
            repeat(3) {
                val id = it + 1
                add(KeystrokeUIState(id, "Key $id", "Desc $id", false))
            }
        }
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog(items = items, initialKeystrokeId = null)
        }

        composeTestRule.onNodeWithText(noKeystroke).assertIsSelected()
    }

    // Given: a long list of Keystrokes that doesn't fit into screen and initially selected
    // keystroke id at the end of a that list.
    // Expected: dialog should scroll to the selected Keystroke.
    @Test
    fun keystrokeOption_needsScrolling_isSelected() {
        val count = 100
        val items = buildList {
            repeat(count) {
                val id = it + 1
                add(KeystrokeUIState(id, "Key $id", "Desc $id", false))
            }
        }
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog(items = items, initialKeystrokeId = count)
        }

        composeTestRule.onNodeWithText("Key $count").apply {
            assertIsDisplayed()
            assertIsSelected()
        }
    }

    @Test
    fun cancelButton_onClick_dismisses() {
        var dismissed = false
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog(onDismiss = { dismissed = true })
        }

        composeTestRule.onNodeWithText(cancel).performClick()

        assertTrue(dismissed)
    }

    @Test
    fun okButton_onClick_confirmsWithCorrectId() {
        val count = 3
        var actual: Int? = -1
        composeTestRule.setContent {
            val items = buildList {
                repeat(count) {
                    val id = it + 1
                    add(KeystrokeUIState(id, "Key $id", "Desc $id", false))
                }
            }
            CreateKeystrokeSelectDialog(
                items = items,
                initialKeystrokeId = 1,
                onConfirm = { actual = it },
            )
        }

        composeTestRule.apply {
            onNodeWithText("Key $count").performClick()
            onNodeWithText(ok).performClick()
        }

        assertEquals(count, actual)
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun CreateKeystrokeSelectDialog(
        title: String = dialogTitle,
        items: List<KeystrokeUIState> = emptyList(),
        initialKeystrokeId: Int? = null,
        onConfirm: (Int?) -> Unit = {},
        onDismiss: () -> Unit = {},
    ) {
        SoundRemoteTheme {
            KeystrokeSelectDialog(
                title = title,
                items = items,
                initialKeystrokeId = initialKeystrokeId,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
            )
        }
    }
}