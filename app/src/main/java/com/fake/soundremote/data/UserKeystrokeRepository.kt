package com.fake.soundremote.data

import com.fake.soundremote.data.room.KeystrokeDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserKeystrokeRepository(
    private val keystrokeDao: KeystrokeDao,
    private val dispatcher: CoroutineDispatcher,
) : KeystrokeRepository {
    @Inject
    constructor(keystrokeDao: KeystrokeDao) :
            this(keystrokeDao, Dispatchers.IO)

    override suspend fun getById(id: Int): Keystroke? = withContext(dispatcher) {
        keystrokeDao.getById(id)
    }

    override suspend fun insert(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.insert(keystroke)
    }

    override suspend fun update(keystroke: Keystroke) = withContext(dispatcher) {
        keystrokeDao.update(keystroke)
    }

    override suspend fun deleteById(id: Int) = withContext(dispatcher) {
        keystrokeDao.deleteById(id)
    }

    override suspend fun changeFavoured(id: Int, favoured: Boolean) = withContext(dispatcher) {
        keystrokeDao.changeFavoured(id, favoured)
    }

    override fun getFavouredOrdered(favoured: Boolean): Flow<List<KeystrokeInfo>> =
        keystrokeDao.getFavouredOrdered(favoured)

    override fun getAllOrdered(): Flow<List<Keystroke>> =
        keystrokeDao.getAllOrdered()

    override fun getAllInfoOrdered(): Flow<List<KeystrokeInfo>> =
        keystrokeDao.getAllInfoOrdered()

    override suspend fun updateOrders(keystrokeOrders: List<KeystrokeOrder>) =
        withContext(dispatcher) {
            keystrokeDao.updateOrders(*keystrokeOrders.toTypedArray())
        }
}
