package com.fake.soundremote.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fake.soundremote.R
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.ui.util.ListItemHeadline
import com.fake.soundremote.ui.util.ListItemSupport
import com.fake.soundremote.ui.util.NavigateUpButton
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_PORT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    settings: SettingsUIState,
    onSetServerPort: (Int) -> Unit,
    onSetClientPort: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val validPorts = 1024..49151

    Column(modifier) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings_title)) },
            navigationIcon = { NavigateUpButton(onNavigateUp) },
        )
        IntPreference(
            title = stringResource(R.string.pref_server_port_title),
            summary = stringResource(R.string.pref_server_port_summary),
            value = settings.serverPort,
            onUpdate = { onSetServerPort(it) },
            validValues = validPorts,
            defaultValue = DEFAULT_SERVER_PORT,
        )
        IntPreference(
            title = stringResource(R.string.pref_client_port_title),
            summary = stringResource(R.string.pref_client_port_summary),
            value = settings.clientPort,
            onUpdate = { onSetClientPort(it) },
            validValues = validPorts,
            defaultValue = DEFAULT_CLIENT_PORT,
        )
    }
}

@Composable
private fun IntPreference(
    title: String,
    summary: String,
    value: Int,
    onUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    validValues: IntRange? = null,
    defaultValue: Int? = null,
) {
    var showEdit by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier.clickable(onClick = { showEdit = true })
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)
    ) {
        val summaryText = if (defaultValue == null) {
            stringResource(R.string.pref_summary_template_short).format(value, summary)
        } else {
            val defaultValueText = stringResource(R.string.pref_default_value_template)
                .format(defaultValue)
            stringResource(R.string.pref_summary_template)
                .format(value, summary, defaultValueText)
        }
        ListItemHeadline(title)
        ListItemSupport(summaryText)
    }
    if (showEdit) {
        var editValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(value.toString()))
        }
        val isValidValue by remember {
            derivedStateOf {
                validValues?.contains(editValue.text.toIntOrNull()) ?: true
            }
        }
        AlertDialog(
            onDismissRequest = { showEdit = false },
            title = { Text(title) },
            text = {
                val editFocusRequester = remember { FocusRequester() }
                SideEffect {
                    editFocusRequester.requestFocus()
                }
                TextField(
                    value = editValue,
                    onValueChange = { newEditValue ->
                        editValue = cleanUIntInput(newEditValue, editValue) ?: return@TextField
                    },
                    supportingText = {
                        if (validValues != null) {
                            Text(
                                stringResource(R.string.pref_valid_int_range_template)
                                    .format(validValues.first, validValues.last)
                            )
                        }
                    },
                    isError = !isValidValue,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.focusRequester(editFocusRequester)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdate(editValue.text.toInt())
                        showEdit = false
                    },
                    enabled = isValidValue,
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEdit = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private fun cleanUIntInput(newValue: TextFieldValue, oldValue: TextFieldValue): TextFieldValue? {
    if (newValue.text == oldValue.text) return newValue
    val newText = newValue.text.filter { it.isDigit() }.trimStart { it == '0' }
    if (newText != oldValue.text) return newValue.copy(text = newText)
    return null
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun SettingsScreenPreview() {
    SoundRemoteTheme {
        SettingsScreen(
            settings = SettingsUIState(1234, 5678),
            onSetClientPort = {},
            onSetServerPort = {},
            onNavigateUp = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IntPreferencePreview() {
    IntPreference(
        title = "Title",
        summary = "This is a very, very long and descriptive summary.",
        value = 1337,
        onUpdate = {},
        defaultValue = 8976,
    )
}

@Preview(showBackground = true)
@Composable
private fun IntPreferenceNoDefaultPreview() {
    IntPreference(
        title = "Title",
        summary = "This is a very, very long and descriptive summary.",
        value = 1337,
        onUpdate = {},
    )
}
