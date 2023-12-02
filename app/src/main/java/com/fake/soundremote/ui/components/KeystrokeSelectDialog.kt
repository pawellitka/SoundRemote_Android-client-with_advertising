package com.fake.soundremote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fake.soundremote.R
import com.fake.soundremote.ui.keystrokelist.KeystrokeListViewModel
import com.fake.soundremote.ui.keystrokelist.KeystrokeUIState

@Composable
internal fun KeystrokeSelectDialog(
    title: String,
    initialKeystrokeId: Int? = null,
    onConfirm: (Int?) -> Unit,
    onDismiss: () -> Unit,
    viewModel: KeystrokeListViewModel = hiltViewModel()
) {
    val state by viewModel.keystrokeListState.collectAsStateWithLifecycle()

    KeystrokeSelect(
        title = title,
        items = state.keystrokes,
        initialKeystrokeId = initialKeystrokeId,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
private fun KeystrokeSelect(
    title: String,
    items: List<KeystrokeUIState>,
    initialKeystrokeId: Int?,
    onConfirm: (Int?) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedKeystrokeId by rememberSaveable { mutableStateOf(initialKeystrokeId) }

    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            KeystrokeList(
                items = items,
                selectedKeystrokeId = selectedKeystrokeId,
                onSelect = { id -> selectedKeystrokeId = id },
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedKeystrokeId) }
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun KeystrokeList(
    items: List<KeystrokeUIState>,
    selectedKeystrokeId: Int?,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    var prescrolled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(items) {
        // Scroll to initially selected keystroke only once on dialog open
        if (prescrolled || items.isEmpty()) return@LaunchedEffect
        // Add 1 to account for the "No keystroke" option
        val index = items.indexOfFirst { it.id == selectedKeystrokeId } + 1
        lazyListState.scrollToItem(index)
        prescrolled = true
    }

    LazyColumn(
        modifier = modifier
            .wrapContentHeight()
            .padding(top = 8.dp, bottom = 8.dp),
        state = lazyListState
    ) {
        item(key = null) {
            KeystrokeItem(
                name = stringResource(R.string.keystroke_select_none),
                description = "",
                selected = selectedKeystrokeId == null,
                onClick = { onSelect(null) },
            )
        }
        items(
            items = items,
            key = { keystroke -> keystroke.id }
        ) { keystroke ->
            KeystrokeItem(
                name = keystroke.name,
                description = keystroke.description,
                selected = keystroke.id == selectedKeystrokeId,
                onClick = { onSelect(keystroke.id) }
            )
        }
    }
}

@Composable
private fun KeystrokeItem(
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .height(72.dp)
            .fillMaxWidth()
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(16.dp))
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            ListItemHeadline(text = name)
            if (description.isNotBlank()) {
                ListItemSupport(text = description)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KeystrokeItemPreview() {
    KeystrokeItem(
        name = "Keystroke name",
        description = "Description",
        selected = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun KeystrokeItemNoDescriptionPreview() {
    KeystrokeItem(
        name = "Keystroke name",
        description = "",
        selected = true,
        onClick = {}
    )
}

@Preview(showBackground = true, device = "id:Nexus S")
@Composable
private fun KeystrokeDialogPreview() {
    KeystrokeSelect(
        title = "Select keystroke",
        items = List(10) {
            KeystrokeUIState(it, "Keystroke $it", "Ctrl + Shift + $it", false)
        },
        initialKeystrokeId = 9,
        onConfirm = {},
        onDismiss = {},
    )
}
