package com.fake.soundremote.data

import kotlinx.coroutines.flow.Flow

interface EventActionRepository {
    suspend fun getById(id: Int): EventAction?

    suspend fun insert(eventAction: EventAction): Long

    suspend fun update(eventAction: EventAction): Int

    suspend fun deleteById(id: Int)

    suspend fun getKeystrokeByEventId(id: Int): Keystroke?

    fun getAll(): Flow<List<EventAction>>
}
