package com.fake.soundremote.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.KeystrokeInfo
import com.fake.soundremote.data.KeystrokeOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface KeystrokeDao : BaseDao<Keystroke> {
    @Query("SELECT * FROM ${Keystroke.TABLE_NAME} WHERE ${Keystroke.COLUMN_ID} = :id")
    suspend fun getById(id: Int): Keystroke?

    @Query("DELETE FROM ${Keystroke.TABLE_NAME} WHERE ${Keystroke.COLUMN_ID} = :id")
    suspend fun deleteById(id: Int)

    @Query(
        """
            UPDATE ${Keystroke.TABLE_NAME}
            SET ${Keystroke.COLUMN_FAVOURED} = :favoured
            WHERE ${Keystroke.COLUMN_ID} = :id;
        """
    )
    suspend fun changeFavoured(id: Int, favoured: Boolean)

    @Query(
        """
        SELECT * FROM ${Keystroke.TABLE_NAME}
        ORDER BY ${Keystroke.COLUMN_ORDER} DESC;
        """
    )
    suspend fun getAllOrderedOneshot(): List<Keystroke>

    @Query(
        """
        SELECT 
        ${Keystroke.COLUMN_ID},
        ${Keystroke.COLUMN_KEY_CODE},
        ${Keystroke.COLUMN_MODS},
        ${Keystroke.COLUMN_NAME}
        FROM ${Keystroke.TABLE_NAME}
        WHERE ${Keystroke.COLUMN_FAVOURED} = :favoured
        ORDER BY ${Keystroke.COLUMN_ORDER} DESC; 
        """
    )
    fun getFavouredOrdered(favoured: Boolean): Flow<List<KeystrokeInfo>>

    @Query(
        """
        SELECT * FROM ${Keystroke.TABLE_NAME}
        ORDER BY ${Keystroke.COLUMN_ORDER} DESC;
        """
    )
    fun getAllOrdered(): Flow<List<Keystroke>>

    @Update(entity = Keystroke::class)
    suspend fun updateOrders(vararg keystrokeOrders: KeystrokeOrder)
}
