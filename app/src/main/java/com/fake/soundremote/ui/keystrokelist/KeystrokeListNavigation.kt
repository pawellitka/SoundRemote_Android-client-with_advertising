package com.fake.soundremote.ui.keystrokelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val KEYSTROKE_LIST_ROUTE = "keystroke_list"

fun NavController.navigateToKeystrokeList() {
    navigate(KEYSTROKE_LIST_ROUTE)
}

fun NavGraphBuilder.keystrokeListScreen(
    onNavigateToKeystrokeCreate: () -> Unit,
    onNavigateToKeystrokeEdit: (keystrokeId: Int) -> Unit,
    onNavigateUp: () -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable(KEYSTROKE_LIST_ROUTE) {
        val viewModel: KeystrokeListViewModel = hiltViewModel()
        val state by viewModel.keystrokeListState.collectAsStateWithLifecycle()
        val lifecycleOwner = LocalLifecycleOwner.current
        KeystrokeListScreen(
            state = state,
            onNavigateToKeystrokeCreate = {
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    onNavigateToKeystrokeCreate()
                }
            },
            onNavigateToKeystrokeEdit = { keystrokeId ->
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    onNavigateToKeystrokeEdit(keystrokeId)
                }
            },
            onDelete = { viewModel.deleteKeystroke(it) },
            onChangeFavoured = { keystrokeId, favoured ->
                viewModel.changeFavoured(keystrokeId, favoured)
            },
            onMove = { fromIndex: Int, toIndex: Int ->
                viewModel.moveKeystroke(fromIndex, toIndex)
            },
            onNavigateUp = onNavigateUp,
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}
