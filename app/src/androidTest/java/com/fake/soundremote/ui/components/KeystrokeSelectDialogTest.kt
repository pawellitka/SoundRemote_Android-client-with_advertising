package com.fake.soundremote.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.fake.soundremote.R
import com.fake.soundremote.stringResource
import com.fake.soundremote.ui.keystrokelist.KeystrokeUIState
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import org.junit.Rule
import org.junit.Test

class KeystrokeSelectDialogTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val dialogTitle by composeTestRule.stringResource(R.string.keystroke_select_title)
    private val noKeystroke by composeTestRule.stringResource(R.string.keystroke_select_none)

    // "No keystroke" option should be selected when there are no other options
    @Test
    fun noKeystrokeOption_noKeystrokes_isSelected() {
        composeTestRule.setContent {
            CreateKeystrokeSelectDialog()
        }

        composeTestRule.onNodeWithText(noKeystroke).assertIsDisplayed()
        composeTestRule.onNodeWithText(noKeystroke).assertIsSelected()
    }

    // "No keystroke" option should be selected when `initialKeystrokeId` is null
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