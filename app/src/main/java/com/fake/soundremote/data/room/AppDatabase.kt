package com.fake.soundremote.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fake.soundremote.data.EventAction
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.KeystrokeOrder

@Database(
    entities = [
        Keystroke::class,
        KeystrokeOrder::class,
        EventAction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keystrokeDao(): KeystrokeDao
    abstract fun keystrokeOrderDao(): KeystrokeOrderDao
    abstract fun eventActionDao(): EventActionDao

    class Callback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                """
                    CREATE TRIGGER order_create AFTER INSERT
                    ON ${Keystroke.TABLE_NAME} FOR EACH ROW
                    BEGIN
                    INSERT INTO ${KeystrokeOrder.TABLE_NAME}
                        ( ${KeystrokeOrder.COLUMN_KEYSTROKE_ID}, '${KeystrokeOrder.COLUMN_ORDER}' )
                        VALUES ( new.${Keystroke.COLUMN_ID}, ${KeystrokeOrder.ORDER_DEFAULT_VALUE} );
                    END
                    """
            )
        }
    }
}