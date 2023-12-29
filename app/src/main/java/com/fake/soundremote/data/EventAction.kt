package com.fake.soundremote.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = EventAction.TABLE_NAME,
)
data class EventAction internal constructor(
    @ColumnInfo(name = COLUMN_ID) @PrimaryKey
    var eventId: Int,

    @ColumnInfo(name = COLUMN_ACTION_TYPE)
    var actionType: Int,

    @ColumnInfo(name = COLUMN_ACTION_ID)
    var actionId: Int,
) {
    companion object {
        const val TABLE_NAME = "event_action"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_ACTION_TYPE = "action_type"
        const val COLUMN_ACTION_ID = "action_id"
        const val COLUMN_KEYSTROKE_ID = "keystroke_id"
    }
}
