package com.fake.soundremote.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = EventAction.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Keystroke::class,
        parentColumns = arrayOf(Keystroke.COLUMN_ID),
        childColumns = arrayOf(EventAction.COLUMN_KEYSTROKE_ID),
        onDelete = CASCADE,
    )]
)
data class EventAction internal constructor(
    @field:ColumnInfo(name = COLUMN_ID) @field:PrimaryKey
    var eventId: Int,

    @field:ColumnInfo(
        name = COLUMN_KEYSTROKE_ID,
        index = true,
    ) var keystrokeId: Int
) {

    companion object {
        const val TABLE_NAME = "event_action"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_ACTION_TYPE = "action_type"
        const val COLUMN_ACTION_ID = "action_id"
        const val COLUMN_KEYSTROKE_ID = "keystroke_id"
    }
}
