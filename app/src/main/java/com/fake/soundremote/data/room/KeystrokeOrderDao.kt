package com.fake.soundremote.data.room

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fake.soundremote.data.KeystrokeOrder

@Dao
interface KeystrokeOrderDao : BaseDao<KeystrokeOrder> {
    @Query(
        """
        SELECT * FROM ${KeystrokeOrder.TABLE_NAME}
        WHERE ${KeystrokeOrder.COLUMN_KEYSTROKE_ID} = :keystrokeId
        """
    )
    suspend fun getByKeystrokeId(keystrokeId: Int): KeystrokeOrder?

    @Query("SELECT * FROM " + KeystrokeOrder.TABLE_NAME)
    suspend fun getAllOneshot(): List<KeystrokeOrder>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(keystrokeOrders: List<KeystrokeOrder>)

    @Query("SELECT COUNT(*) FROM ${KeystrokeOrder.TABLE_NAME}")
    suspend fun count(): Int
}