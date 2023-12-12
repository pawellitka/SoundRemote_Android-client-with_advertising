package com.fake.soundremote.ui.keystroke

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fake.soundremote.util.ModKey

const val KEYSTROKE_ID_ARG = "keystrokeId"
private const val KEYSTROKE_CREATE_ROUTE = "keystroke_create"
private const val KEYSTROKE_EDIT_PREFIX = "keystroke_edit/"
private const val KEYSTROKE_EDIT_ROUTE = "$KEYSTROKE_EDIT_PREFIX{$KEYSTROKE_ID_ARG}"

fun NavController.navigateToKeystrokeCreate() {
    navigate(KEYSTROKE_CREATE_ROUTE)
}

fun NavController.navigateToKeystrokeEdit(keystrokeId: Int) {
    navigate("$KEYSTROKE_EDIT_PREFIX$keystrokeId")
}

fun NavGraphBuilder.keystrokeCreateScreen(
    onNavigateUp: () -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
    compactHeight: Boolean,
) {
    composable(KEYSTROKE_CREATE_ROUTE) {
        KeystrokeScreenRoute(
            onNavigateUp = onNavigateUp,
            showSnackbar = showSnackbar,
            compactHeight = compactHeight
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}

internal class KeystrokeEditArgs(val keystrokeId: Int?) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<Int?>(KEYSTROKE_ID_ARG))
}

fun NavGraphBuilder.keystrokeEditScreen(
    onNavigateUp: () -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
    compactHeight: Boolean,
) {
    composable(
        route = KEYSTROKE_EDIT_ROUTE,
        arguments = listOf(navArgument(KEYSTROKE_ID_ARG) { type = NavType.IntType }),
    ) {
        KeystrokeScreenRoute(
            onNavigateUp = onNavigateUp,
            showSnackbar = showSnackbar,
            compactHeight = compactHeight
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}

@Composable
private fun KeystrokeScreenRoute(
    onNavigateUp: () -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    compactHeight: Boolean,
) {
    val viewModel: KeystrokeViewModel = hiltViewModel()
    val state by viewModel.keystrokeScreenState.collectAsStateWithLifecycle()
    KeystrokeScreen(
        state = state,
        onKeyCodeChange = { viewModel.updateKeyCode(it) },
        onWinChange = { viewModel.updateMod(ModKey.WIN, it) },
        onCtrlChange = { viewModel.updateMod(ModKey.CTRL, it) },
        onShiftChange = { viewModel.updateMod(ModKey.SHIFT, it) },
        onAltChange = { viewModel.updateMod(ModKey.ALT, it) },
        onNameChange = { viewModel.updateName(it) },
        checkCanSave = viewModel::canSave,
        onSave = viewModel::saveKeystroke,
        onClose = onNavigateUp,
        showSnackbar = showSnackbar,
        compactHeight = compactHeight,
    )
}
