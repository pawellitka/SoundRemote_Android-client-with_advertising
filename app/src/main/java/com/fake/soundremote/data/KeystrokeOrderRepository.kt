package com.fake.soundremote.data

import com.fake.soundremote.data.room.KeystrokeOrderDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystrokeOrderRepository(
    private val keystrokeOrderDao: KeystrokeOrderDao,
    private val dispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(keystrokeOrderDao: KeystrokeOrderDao) : this(keystrokeOrderDao, Dispatchers.IO)

    suspend fun getByKeystrokeId(keystrokeId: Int): KeystrokeOrder? = withContext(dispatcher) {
        keystrokeOrderDao.getByKeystrokeId(keystrokeId)
    }

    suspend fun getAllOneshot(): List<KeystrokeOrder> = withContext(dispatcher) {
        keystrokeOrderDao.getAllOneshot()
    }

    suspend fun update(keystrokeOrders: List<KeystrokeOrder>) = withContext(dispatcher) {
        keystrokeOrderDao.updateAll(keystrokeOrders)
    }

    suspend fun count() = withContext(dispatcher) {
        keystrokeOrderDao.count()
    }
}