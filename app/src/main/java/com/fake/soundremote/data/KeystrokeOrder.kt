package com.fake.soundremote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = KeystrokeOrder.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Keystroke::class,
        parentColumns = arrayOf(Keystroke.COLUMN_ID),
        childColumns = arrayOf(KeystrokeOrder.COLUMN_KEYSTROKE_ID),
        onDelete = CASCADE
    )]
)
class KeystrokeOrder(
    @field:ColumnInfo(name = COLUMN_KEYSTROKE_ID) @field:PrimaryKey
    var keystrokeId: Int,
    //Keystrokes are ordered by this number descending, so new items with 0 will be below.
    @field:ColumnInfo(name = COLUMN_ORDER, defaultValue = "$ORDER_DEFAULT_VALUE")
    var order: Int
) {

    companion object {
        const val TABLE_NAME = "keystroke_order"
        const val COLUMN_KEYSTROKE_ID = "keystroke_id"
        const val COLUMN_ORDER = "order"
        const val ORDER_DEFAULT_VALUE = 0
    }
}