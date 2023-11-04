package com.fake.soundremote.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fake.soundremote.R
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.ui.util.NavigateUpButton
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_PORT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    settings: SettingsUIState,
    onSetServerPort: (Int) -> Unit,
    onSetClientPort: (Int) -> Unit,
    onSetAudioCompression: (Int) -> Unit,
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

val preferenceItemPadding = Modifier
    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun SettingsScreenPreview() {
    SoundRemoteTheme {
        SettingsScreen(
            settings = SettingsUIState(1234, 5678, 0),
            onSetClientPort = {},
            onSetServerPort = {},
            onSetAudioCompression = {},
            onNavigateUp = {},
        )
    }
}
