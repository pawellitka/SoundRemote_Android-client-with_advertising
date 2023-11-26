package com.fake.soundremote.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestEventActionRepository : EventActionRepository {
    private val _eventActionsFlow = MutableStateFlow<List<EventAction>>(emptyList())

    override suspend fun getById(id: Int): EventAction? {
        return _eventActionsFlow.value.find { it.eventId == id }?.copy()
    }

    override suspend fun insert(eventAction: EventAction) {
        _eventActionsFlow.update { it + eventAction }
    }

    override suspend fun update(eventAction: EventAction): Int {
        _eventActionsFlow.update { events ->
            val indexToUpdate = events.indexOfFirst { it.eventId == eventAction.eventId }
            if (indexToUpdate == -1) return 0
            events.toMutableList().apply {
                set(indexToUpdate, eventAction)
            }
        }
        return 1
    }

    override suspend fun deleteById(id: Int) {
        _eventActionsFlow.update { events ->
            events.toMutableList().apply {
                removeIf { it.eventId == id }
            }
        }
    }

    override suspend fun getKeystrokeByEventId(id: Int): Keystroke? {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<EventAction>> = _eventActionsFlow

    // Test methods
    fun setEventActions(eventActions: List<EventAction>) {
        _eventActionsFlow.value = eventActions
    }
}
