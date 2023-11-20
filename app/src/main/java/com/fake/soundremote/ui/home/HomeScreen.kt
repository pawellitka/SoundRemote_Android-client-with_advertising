package com.fake.soundremote.ui.home

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fake.soundremote.R
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.ui.util.ListItemHeadline
import com.fake.soundremote.ui.util.ListItemSupport
import com.fake.soundremote.util.ConnectionStatus

private val paddingMod = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
private val keystrokeItemModifier = Modifier.fillMaxWidth().height(72.dp).then(paddingMod)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    uiState: HomeUIState,
    @StringRes messageId: Int?,
    onSendKeystroke: (Int) -> Unit,
    onEditKeystroke: (Int) -> Unit,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
    onSetMuted: (Boolean) -> Unit,
    onMessageShown: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Messages
    messageId?.let { id ->
        val message = stringResource(id)
        showSnackbar(message, SnackbarDuration.Short)
        onMessageShown()
    }

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(stringResource(R.string.home_title)) },
            actions = {
                IconToggleButton(
                    checked = uiState.isMuted,
                    onCheckedChange = { onSetMuted(it) }
                ) {
                    if (uiState.isMuted) {
                        Icon(
                            painter = painterResource(R.drawable.sound_off_24),
                            contentDescription = stringResource(R.string.unmute)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.sound_on_24),
                            contentDescription = stringResource(R.string.mute)
                        )
                    }
                }
                Box {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.navigation_menu)
                        )
                    }
                    val menuContentDescription =
                        stringResource(R.string.navigation_menu_description)
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.semantics {
                            contentDescription = menuContentDescription
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_events)) },
                            onClick = {
                                showMenu = false
                                onNavigateToEvents()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_settings)) },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_about)) },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            },
                        )
                    }
                }
            }
        )
        ConnectComponent(
            address = uiState.serverAddress,
            connectionStatus = uiState.connectionStatus,
            onConnect = { onConnect(it) },
            onDisconnect = onDisconnect,
        )
        LazyColumn(
            modifier = Modifier.fillMaxHeight().padding(top = 8.dp, bottom = 8.dp),
        ) {
            items(items = uiState.keystrokes, key = { it.id }) { keystroke ->
                KeystrokeItem(
                    name = keystroke.name,
                    description = keystroke.description,
                    onClick = { onSendKeystroke(keystroke.id) },
                    onLongClick = { onEditKeystroke(keystroke.id) },
                )
            }
        }
    }
}

@Composable
private fun ConnectComponent(
    address: String,
    connectionStatus: ConnectionStatus,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    var addressValue by rememberSaveable(address, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(address))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(paddingMod)
    ) {
        AddressEdit(
            address = addressValue,
            onChange = { newEditValue ->
                addressValue = cleanAddressInput(newEditValue, addressValue) ?: return@AddressEdit
            },
            onConnect = { onConnect(addressValue.text) },
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(16.dp))
        ConnectButton(
            connectionStatus = connectionStatus,
            onConnect = { onConnect(addressValue.text) },
            onDisconnect = onDisconnect,
            // Padding to look aligned with OutlinedTextField vertically
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Filter out leading zeroes and everything else except digits and dots
 */
private fun cleanAddressInput(newValue: TextFieldValue, oldValue: TextFieldValue): TextFieldValue? {
    if (newValue.text == oldValue.text) return newValue
    val newText = newValue.text.filter { it.isDigit() || it == '.' }.trimStart { it == '0' }
    if (newText != oldValue.text) return newValue.copy(text = newText)
    return null
}

@Composable
private fun AddressEdit(
    address: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = address,
        onValueChange = onChange,
        label = { Text(stringResource(R.string.address_label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Go,
            keyboardType = KeyboardType.Number,
        ),
        keyboardActions = KeyboardActions(onAny = { onConnect() }),
        modifier = modifier
    )
}

@Composable
private fun ConnectButton(
    connectionStatus: ConnectionStatus,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectedColor = colorResource(R.color.indicatorConnected)
    Box(modifier = modifier) {
        val statusIndicatorSize = Modifier.size(48.dp)
        when (connectionStatus) {
            ConnectionStatus.DISCONNECTED -> {}

            ConnectionStatus.CONNECTING -> {
                CircularProgressIndicator(
                    modifier = statusIndicatorSize,
                )
            }

            ConnectionStatus.CONNECTED -> {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = statusIndicatorSize,
                    color = connectedColor,
                )
            }
        }
        when (connectionStatus) {
            ConnectionStatus.DISCONNECTED -> {
                IconButton(
                    onClick = onConnect,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.connect_caption),
                    )
                }
            }

            ConnectionStatus.CONNECTING,
            ConnectionStatus.CONNECTED -> {
                val tint = if (connectionStatus == ConnectionStatus.CONNECTED) {
                    connectedColor
                } else {
                    LocalContentColor.current
                }
                IconButton(
                    onClick = onDisconnect,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.disconnect_caption),
                        tint = tint
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeystrokeItem(
    name: String,
    description: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .then(keystrokeItemModifier),
        verticalArrangement = Arrangement.Center,
    ) {
        ListItemHeadline(text = name)
        ListItemSupport(text = description)
    }
}

@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light",
    device = "id:Nexus 5"
)
@Preview(
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark",
    device = "id:Nexus 5"
)
@Composable
private fun ScreenPreview() {
    var status by remember { mutableStateOf(ConnectionStatus.DISCONNECTED) }
    var id = 0
    SoundRemoteTheme {
        HomeScreen(
            uiState = HomeUIState(
                serverAddress = "192.168.0.1",
                keystrokes = listOf(
                    HomeKeystrokeUIState(++id, "Pause", "Media Pause"),
                    HomeKeystrokeUIState(++id, "Volume up", "Volume Up"),
                ),
                connectionStatus = status,
                isMuted = true,
            ),
            messageId = null,
            onEditKeystroke = {},
            onConnect = { status = ConnectionStatus.CONNECTING },
            onDisconnect = {
                status = if (status == ConnectionStatus.CONNECTING) {
                    ConnectionStatus.CONNECTED
                } else {
                    ConnectionStatus.DISCONNECTED
                }
            },
            onSetMuted = {},
            onSendKeystroke = {},
            onMessageShown = {},
            onNavigateToEvents = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {},
            showSnackbar = { _, _ -> },
        )
    }
}
