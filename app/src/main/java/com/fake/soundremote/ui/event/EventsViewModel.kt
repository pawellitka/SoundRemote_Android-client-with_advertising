package com.fake.soundremote.ui.event

import android.os.Build
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.Event
import com.fake.soundremote.data.EventAction
import com.fake.soundremote.data.EventActionRepository
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.KeystrokeRepository
import com.fake.soundremote.util.AppPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class EventUIState(
    val id: Int,
    @StringRes
    val nameStringId: Int,
    val permission: AppPermission? = null,
    val keystrokeId: Int? = null,
    val keystrokeName: String? = null,
)

internal data class EventListUIState(
    val events: List<EventUIState> = emptyList()
)

@HiltViewModel
internal class EventsViewModel @Inject constructor(
    private val eventActionRepository: EventActionRepository,
    private val keystrokeRepository: KeystrokeRepository,
) : ViewModel() {
    private val _eventsUIState = MutableStateFlow(EventListUIState())
    val uiState: StateFlow<EventListUIState>
        get() = _eventsUIState

    init {
        viewModelScope.launch {
            val eventFlow = flowOf(Event.values())
            val eventActionsFlow = eventActionRepository.getAll()
            combine(eventFlow, eventActionsFlow) { eventList, eventActions ->
                val eventUIStates = mutableListOf<EventUIState>()
                for (event in eventList) {
                    // Only use repository for events that have a bound keystroke
                    val keystroke: Keystroke? = eventActions.find { it.eventId == event.id }
                        ?.keystrokeId?.let { keystrokeRepository.getById(it) }
                    val permission = if (
                        (event.permissionMinSdk == null) ||
                        (event.permissionMinSdk <= Build.VERSION.SDK_INT)
                    ) {
                        event.requiredPermission
                    } else {
                        null
                    }
                    eventUIStates.add(
                        EventUIState(
                            id = event.id,
                            nameStringId = event.nameStringId,
                            permission = permission,
                            keystrokeId = keystroke?.id,
                            keystrokeName = keystroke?.name
                        )
                    )
                }
                EventListUIState(eventUIStates)
            }.collect { _eventsUIState.value = it }
        }
    }

    fun setKeystrokeForEvent(eventId: Int, keystrokeId: Int?) {
        viewModelScope.launch {
            if (keystrokeId == null) {
                eventActionRepository.deleteById(eventId)
            } else {
                val event = eventActionRepository.getById(eventId)
                if (event == null) {
                    eventActionRepository.insert(EventAction(eventId, keystrokeId))
                } else {
                    event.keystrokeId = keystrokeId
                    eventActionRepository.update(event)
                }
            }
        }
    }
}
