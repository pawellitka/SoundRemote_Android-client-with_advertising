package com.fake.soundremote.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestEventActionRepository : EventActionRepository {
    private val eventActionsFlow: MutableSharedFlow<List<EventAction>> = MutableSharedFlow(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentEventActions: List<EventAction>
        get() = eventActionsFlow.replayCache.firstOrNull() ?: emptyList()

    override suspend fun getById(id: Int): EventAction? {
        return currentEventActions.find { it.eventId == id }?.copy()
    }

    override suspend fun insert(eventAction: EventAction) {
        val existing = currentEventActions.find { it.eventId == eventAction.eventId }
        if (existing == null) {
            eventActionsFlow.tryEmit(currentEventActions + eventAction)
        } else {
            existing.keystrokeId = eventAction.keystrokeId
            eventActionsFlow.tryEmit(currentEventActions)
        }
    }

    override suspend fun update(eventAction: EventAction): Int {
        val toUpdate = currentEventActions.find { it.eventId == eventAction.eventId } ?: return 0
        toUpdate.keystrokeId = eventAction.keystrokeId
        eventActionsFlow.tryEmit(currentEventActions)
        return 1
    }

    override suspend fun deleteById(id: Int) {
        val alteredList = currentEventActions.toMutableList()
        // If nothing to remove, do not emit
        if (!alteredList.removeIf { it.eventId == id }) return
        eventActionsFlow.tryEmit(alteredList)
    }

    override suspend fun getKeystrokeByEventId(id: Int): Keystroke? {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<EventAction>> = eventActionsFlow

    // Test methods
    fun setEventActions(eventActions: List<EventAction>) {
        eventActionsFlow.tryEmit(eventActions)
    }
}
