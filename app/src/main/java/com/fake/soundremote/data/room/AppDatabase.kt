package com.fake.soundremote.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fake.soundremote.data.ActionType
import com.fake.soundremote.data.EventAction
import com.fake.soundremote.data.Keystroke

@Database(
    entities = [
        Keystroke::class,
        EventAction::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = DatabaseMigrations.Schema1to2::class),
    ],
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keystrokeDao(): KeystrokeDao
    abstract fun eventActionDao(): EventActionDao

    class Callback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(DELETE_EVENT_ACTION_ON_KEYSTROKE_DELETE)
        }
    }
}

// When a keystroke is deleted also delete all event actions with that keystroke
val DELETE_EVENT_ACTION_ON_KEYSTROKE_DELETE = """
    CREATE TRIGGER IF NOT EXISTS delete_event_action_on_keystroke_delete
    AFTER DELETE ON ${Keystroke.TABLE_NAME}
    BEGIN
        DELETE FROM ${EventAction.TABLE_NAME}
        WHERE ${EventAction.COLUMN_ACTION_TYPE} = ${ActionType.KEYSTROKE.id}
        AND ${EventAction.COLUMN_ACTION_ID} = OLD.${Keystroke.COLUMN_ID};
    END
    """.trimIndent()
