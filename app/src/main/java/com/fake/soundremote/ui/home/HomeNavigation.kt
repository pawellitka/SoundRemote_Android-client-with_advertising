package com.fake.soundremote.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fake.soundremote.R

const val homeRoute = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToKeystrokeList: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onEditKeystroke: (keystrokeId: Int) -> Unit,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable(homeRoute) {
        val viewModel: MainViewModel = hiltViewModel()
        val homeUIState by viewModel.homeUIState.collectAsStateWithLifecycle()
        HomeScreen(
            uiState = homeUIState,
            messageId = viewModel.messageState,
            onEditKeystroke = onEditKeystroke,
            onConnect = { viewModel.connect(it) },
            onDisconnect = viewModel::disconnect,
            onSendKeystroke = { viewModel.sendKeystroke(it) },
            onSetMuted = { viewModel.setMuted(it) },
            onMessageShown = viewModel::messageShown,
            onNavigateToEvents = onNavigateToEvents,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAbout = onNavigateToAbout,
            showSnackbar = showSnackbar,
        )
        LaunchedEffect(Unit) {
            setFab {
                FloatingActionButton(onClick = onNavigateToKeystrokeList) {
                    Icon(Icons.Default.Edit, stringResource(R.string.action_edit_keystrokes))
                }
            }
        }
    }
}
