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

    @Query(
        """           
        SELECT k.* FROM ${Keystroke.TABLE_NAME} AS k
        JOIN ${EventAction.TABLE_NAME} AS e
        ON e.${EventAction.COLUMN_KEYSTROKE_ID} = k.${Keystroke.COLUMN_ID}
        WHERE e.${EventAction.COLUMN_ID} = :id
        """
    )
    suspend fun getKeystrokeByEventId(id: Int): Keystroke?

    @Query("SELECT * FROM ${EventAction.TABLE_NAME}")
    fun getAll(): Flow<List<EventAction>>
}