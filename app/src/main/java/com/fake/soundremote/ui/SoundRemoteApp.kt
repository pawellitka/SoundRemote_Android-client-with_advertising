package com.fake.soundremote.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
internal fun SoundRemoteApp(
    windowSizeClass: WindowSizeClass,
    viewModel: AppViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val (fab, setFab) = remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    val systemMessageId by viewModel.systemMessage.collectAsStateWithLifecycle()

    // Binding and unbinding the connection
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.bindConnection(context)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.unbindConnection(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    systemMessageId?.let { id ->
        val message = stringResource(id)
        LaunchedEffect(id) {
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { fab?.invoke() }
    ) { paddingValues ->
        AppNavigation(
            windowSizeClass,
            showSnackbar = { message, duration ->
                scope.launch {
                    snackbarHostState.showSnackbar(message = message, duration = duration)
                }
            },
            setFab = setFab,
            padding = paddingValues,
        )
    }
}
