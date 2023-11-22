package com.fake.soundremote.data

import com.fake.soundremote.data.room.KeystrokeDao
import com.fake.soundremote.data.room.KeystrokeOrderDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystrokeRepository(
    private val keystrokeDao: KeystrokeDao,
    private val keystrokeOrderDao: KeystrokeOrderDao,
    private val dispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(keystrokeDao: KeystrokeDao, keystrokeOrderDao: KeystrokeOrderDao) :
            this(keystrokeDao, keystrokeOrderDao, Dispatchers.IO)

    suspend fun getById(id: Int): Keystroke? = withContext(dispatcher) {
        keystrokeDao.getById(id)
    }

    suspend fun insert(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.insert(keystroke)
    }

    suspend fun update(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.update(keystroke)
    }

    suspend fun delete(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.delete(keystroke)
    }

    suspend fun deleteById(id: Int) = withContext(dispatcher) {
        keystrokeDao.deleteById(id)
    }

    suspend fun changeFavoured(id: Int, favoured: Boolean) = withContext(dispatcher) {
        keystrokeDao.changeFavoured(id, favoured)
    }

    suspend fun getOrderedOneshot(): List<Keystroke> = withContext(dispatcher) {
        keystrokeDao.getOrderedOneshot()
    }

    fun getFavouredOrdered(favoured: Boolean): Flow<List<Keystroke>> =
        keystrokeDao.getFavouredOrdered(favoured)

    fun getAllOrdered(): Flow<List<Keystroke>> =
        keystrokeDao.getAllOrdered()

    suspend fun getAllOrdersOneshot(): List<KeystrokeOrder> = withContext(dispatcher) {
        keystrokeOrderDao.getAllOneshot()
    }

    suspend fun updateOrders(keystrokeOrders: List<KeystrokeOrder>) = withContext(dispatcher) {
        keystrokeOrderDao.updateAll(keystrokeOrders)
    }
}
