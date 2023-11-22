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
class UserKeystrokeRepository(
    private val keystrokeDao: KeystrokeDao,
    private val keystrokeOrderDao: KeystrokeOrderDao,
    private val dispatcher: CoroutineDispatcher,
) : KeystrokeRepository {
    @Inject
    constructor(keystrokeDao: KeystrokeDao, keystrokeOrderDao: KeystrokeOrderDao) :
            this(keystrokeDao, keystrokeOrderDao, Dispatchers.IO)

    override suspend fun getById(id: Int): Keystroke? = withContext(dispatcher) {
        keystrokeDao.getById(id)
    }

    override suspend fun insert(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.insert(keystroke)
    }

    override suspend fun update(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.update(keystroke)
    }

    override suspend fun delete(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.delete(keystroke)
    }

    override suspend fun deleteById(id: Int) = withContext(dispatcher) {
        keystrokeDao.deleteById(id)
    }

    override suspend fun changeFavoured(id: Int, favoured: Boolean) = withContext(dispatcher) {
        keystrokeDao.changeFavoured(id, favoured)
    }

    override suspend fun getOrderedOneshot(): List<Keystroke> = withContext(dispatcher) {
        keystrokeDao.getOrderedOneshot()
    }

    override fun getFavouredOrdered(favoured: Boolean): Flow<List<Keystroke>> =
        keystrokeDao.getFavouredOrdered(favoured)

    override fun getAllOrdered(): Flow<List<Keystroke>> =
        keystrokeDao.getAllOrdered()

    override suspend fun getAllOrdersOneshot(): List<KeystrokeOrder> = withContext(dispatcher) {
        keystrokeOrderDao.getAllOneshot()
    }

    override suspend fun updateOrders(keystrokeOrders: List<KeystrokeOrder>) =
        withContext(dispatcher) {
            keystrokeOrderDao.updateAll(keystrokeOrders)
        }
}
