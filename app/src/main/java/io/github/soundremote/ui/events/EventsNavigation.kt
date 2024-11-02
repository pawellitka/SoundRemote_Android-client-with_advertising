package io.github.soundremote.ui.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val EVENTS_ROUTE = "events"

fun NavController.navigateToEvents() {
    navigate(EVENTS_ROUTE)
}

fun NavGraphBuilder.eventsScreen(
    onNavigateUp: () -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable(EVENTS_ROUTE) {
        val viewModel: EventsViewModel = hiltViewModel()
        val eventsUIState by viewModel.uiState.collectAsStateWithLifecycle()
        EventsScreen(
            eventsUIState = eventsUIState,
            onSetActionForEvent = { eventId, action ->
                viewModel.setActionForEvent(eventId, action)
            },
            onNavigateUp = onNavigateUp,
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}
