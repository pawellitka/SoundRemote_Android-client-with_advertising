package com.fake.soundremote.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val EVENT_LIST_ROUTE = "event_list"

fun NavController.navigateToEventList() {
    navigate(EVENT_LIST_ROUTE)
}

fun NavGraphBuilder.eventListScreen(
    onNavigateUp: () -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable(EVENT_LIST_ROUTE) {
        val viewModel: EventsViewModel = hiltViewModel()
        val eventListState by viewModel.uiState.collectAsStateWithLifecycle()
        EventListScreen(
            eventListState = eventListState,
            onSetKeystrokeForEvent = { eventId, keystrokeId ->
                viewModel.setKeystrokeForEvent(eventId = eventId, keystrokeId = keystrokeId)
            },
            onNavigateUp = onNavigateUp,
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}