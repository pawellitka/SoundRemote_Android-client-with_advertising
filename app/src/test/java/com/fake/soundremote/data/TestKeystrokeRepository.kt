package com.fake.soundremote.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestKeystrokeRepository : KeystrokeRepository {
    private val _keystrokesFlow: MutableSharedFlow<List<Keystroke>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentKeystrokes get() = _keystrokesFlow.replayCache.firstOrNull() ?: emptyList()

    override suspend fun getById(id: Int): Keystroke? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(keystroke: Keystroke): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(keystroke: Keystroke): Int {
        TODO("Not yet implemented")
    }

    override suspend fun delete(keystroke: Keystroke): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: Int) {
        val newList = currentKeystrokes.toMutableList()
        newList.removeIf { it.id == id }
        _keystrokesFlow.tryEmit(newList)
    }

    override suspend fun changeFavoured(id: Int, favoured: Boolean) {
        val newList = currentKeystrokes
        newList.find { it.id == id }!!.isFavoured = favoured
        _keystrokesFlow.tryEmit(newList)
    }

    override fun getFavouredOrdered(favoured: Boolean): Flow<List<KeystrokeInfo>> {
        TODO("Not yet implemented")
    }

    override fun getAllOrdered(): Flow<List<Keystroke>> {
        return _keystrokesFlow.map { keystrokes ->
            keystrokes.sortedByDescending { it.order }
        }
    }

    override suspend fun getAllOrderedOneshot(): List<Keystroke> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrders(keystrokeOrders: List<KeystrokeOrder>) {
        val keystrokes = currentKeystrokes
        for ((id, order) in keystrokeOrders) {
            keystrokes.find { it.id == id }!!.order = order
        }
        _keystrokesFlow.tryEmit(keystrokes)
    }

    // Test methods
    fun setKeystrokes(keystrokes: List<Keystroke>) {
        _keystrokesFlow.tryEmit(keystrokes)
    }
}
