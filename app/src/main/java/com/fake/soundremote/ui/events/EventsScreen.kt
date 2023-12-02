@file:OptIn(ExperimentalPermissionsApi::class)

package com.fake.soundremote.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fake.soundremote.R
import com.fake.soundremote.data.Event
import com.fake.soundremote.ui.keystrokeselectdialog.KeystrokeSelectDialog
import com.fake.soundremote.ui.components.ListItemHeadline
import com.fake.soundremote.ui.components.ListItemSupport
import com.fake.soundremote.ui.components.NavigateUpButton
import com.fake.soundremote.util.showAppInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun EventsScreen(
    eventsUIState: EventsUIState,
    onSetKeystrokeForEvent: (eventId: Int, keystrokeId: Int?) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showKeystrokeSelect by rememberSaveable { mutableStateOf(false) }
    var selectedEventId: Int? by rememberSaveable { mutableStateOf(null) }
    var selectedKeystrokeId: Int? by rememberSaveable { mutableStateOf(null) }

    val permissionStates = mutableMapOf<String, PermissionState>()
    for (event in eventsUIState.events) {
        event.permission?.also { permission ->
            if (!permissionStates.containsKey(permission.id)) {
                permissionStates[permission.id] = rememberPermissionState(permission.id)
            }
        }
    }

    fun checkAndRequestPermission(eventId: Int, keystrokeId: Int?) {
        val permission = eventsUIState.events.find { it.id == eventId }?.permission
        if (keystrokeId == null || permission == null) return
        val permissionState = permissionStates[permission.id]!!
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    fun onSelectKeystroke(keystrokeId: Int?) {
        val eventId = selectedEventId ?: return
        checkAndRequestPermission(eventId, keystrokeId)
        onSetKeystrokeForEvent(eventId, keystrokeId)
    }

    Events(
        events = eventsUIState.events,
        permissionStates = permissionStates,
        onEventClick = { eventId, keystrokeId ->
            selectedEventId = eventId
            selectedKeystrokeId = keystrokeId
            showKeystrokeSelect = true
        },
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
    if (showKeystrokeSelect) {
        KeystrokeSelectDialog(
            title = stringResource(R.string.keystroke_select_title),
            initialKeystrokeId = selectedKeystrokeId,
            onConfirm = { keystrokeId ->
                onSelectKeystroke(keystrokeId)
                showKeystrokeSelect = false
            },
            onDismiss = {
                showKeystrokeSelect = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Events(
    events: List<EventUIState>,
    permissionStates: Map<String, PermissionState>,
    onEventClick: (Int, Int?) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        TopAppBar(
            title = { Text(stringResource(R.string.event_list_title)) },
            navigationIcon = { NavigateUpButton(onNavigateUp) },
        )
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ) {
            items(
                items = events,
                key = { eventState -> eventState.id }
            ) { eventState ->
                EventItem(
                    eventName = stringResource(eventState.nameStringId),
                    keystrokeName = eventState.keystrokeName,
                    permissionNameId = eventState.permission?.nameStringId,
                    permissionState = permissionStates[eventState.permission?.id],
                    onClick = {
                        onEventClick(eventState.id, eventState.keystrokeId)
                    }
                )
            }
        }
    }
}

private val eventItemModifier = Modifier
    .fillMaxWidth()
    .height(72.dp)
    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)

@Composable
private fun EventItem(
    eventName: String,
    keystrokeName: String?,
    permissionNameId: Int?,
    permissionState: PermissionState?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        Row(
            modifier = modifier
                .clickable(onClick = onClick)
                .then(eventItemModifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                ListItemHeadline(text = eventName)
                ListItemSupport(text = keystrokeName ?: stringResource(R.string.event_no_keystroke))
            }
            if (permissionState != null && permissionNameId != null) {
                PermissionInfo(permissionState, permissionNameId)
            }
        }
        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionInfo(
    permissionState: PermissionState,
    permissionNameStringId: Int,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = remember { RichTooltipState() }

    val permissionText = if (permissionState.status.isGranted) {
        stringResource(R.string.permission_granted_caption)
    } else if (permissionState.status.shouldShowRationale) {
        // If the user has denied the permission but the rationale can be shown.
        stringResource(R.string.permission_required_caption)
    } else {
        // If it's the first time the user lands on this feature, or the user
        // doesn't want to be asked again for this permission,
        stringResource(R.string.permission_denied_caption)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.permission_caption),
                style = typography.labelSmall,
            )
            Text(
                text = permissionText,
                style = typography.labelSmall,
            )
        }
        RichTooltipBox(
            title = { Text(stringResource(R.string.permission_tooltip_title)) },
            text = {
                Text(
                    stringResource(R.string.permission_tooltip_text_template)
                        .format(stringResource(permissionNameStringId))
                )
            },
            action = {
                val context = LocalContext.current
                TextButton(
                    onClick = {
                        showAppInfo(context)
                        scope.launch {
                            tooltipState.dismiss()
                        }
                    }
                ) { Text(stringResource(R.string.app_info)) }
            },
            tooltipState = tooltipState,
        ) {
            IconButton(
                onClick = { scope.launch { tooltipState.show() } },
                modifier = Modifier.tooltipAnchor()
            ) {
                Icon(Icons.Default.Info, stringResource(R.string.permission_show_info_caption))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventItemPreview() {
    EventItem(
        eventName = "Event name",
        keystrokeName = "Keystroke name",
        permissionState = null,
        permissionNameId = null,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun EventsPreview() {
    Events(
        events = Event.values().map { event ->
            EventUIState(
                id = event.id,
                nameStringId = event.nameStringId,
                permission = event.requiredPermission,
                keystrokeId = 0,
                keystrokeName = "Ctrl + Shift + ${event.id}"
            )
        },
        permissionStates = emptyMap(),
        onEventClick = { _, _ -> },
        onNavigateUp = {},
    )
}
