package com.fake.soundremote.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fake.soundremote.data.room.Action

@Entity(
    tableName = EventAction.TABLE_NAME,
)
data class EventAction internal constructor(
    @ColumnInfo(name = COLUMN_ID) @PrimaryKey
    var eventId: Int,

    @Embedded val action: Action
) {
    companion object {
        const val TABLE_NAME = "event_action"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_KEYSTROKE_ID = "keystroke_id"
    }
}
