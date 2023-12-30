package com.fake.soundremote.data.room

import androidx.room.Dao
import androidx.room.Query
import com.fake.soundremote.data.EventAction
import com.fake.soundremote.data.Keystroke
import kotlinx.coroutines.flow.Flow

@Dao
interface EventActionDao : BaseDao<EventAction> {

    @Query("SELECT * FROM ${EventAction.TABLE_NAME} WHERE ${EventAction.COLUMN_ID} = :id")
    suspend fun getById(id: Int): EventAction?


    @Query("DELETE FROM ${EventAction.TABLE_NAME} WHERE ${EventAction.COLUMN_ID} = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ${EventAction.TABLE_NAME}")
    fun getAll(): Flow<List<EventAction>>
}