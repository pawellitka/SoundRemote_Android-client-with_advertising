package com.fake.soundremote.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fake.soundremote.data.EventAction
import com.fake.soundremote.data.Keystroke

@Database(
    entities = [
        Keystroke::class,
        EventAction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keystrokeDao(): KeystrokeDao
    abstract fun eventActionDao(): EventActionDao
}
