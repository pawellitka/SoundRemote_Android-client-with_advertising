package com.fake.soundremote.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fake.soundremote.util.generateDescription

@Entity(tableName = Keystroke.TABLE_NAME)
data class Keystroke(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Int = 0,
    @ColumnInfo(name = COLUMN_KEY_CODE)
    var keyCode: Int,
    @ColumnInfo(name = COLUMN_MODS)
    var mods: Int,
    @ColumnInfo(name = COLUMN_NAME)
    var name: String,
    @ColumnInfo(name = COLUMN_FAVOURED)
    var isFavoured: Boolean
) {
    /**
     * Creates Keystroke
     *
     * @param keyCode  key's windows Virtual-Key code
     * @param name     text description of the keystroke
     * @param mods     mods bitfield
     * @param favoured show the keystroke in the main app screen
     */
    @Ignore
    constructor(keyCode: Int, name: String, mods: Int? = null, favoured: Boolean = true) :
            this(
                keyCode = keyCode,
                name = name,
                mods = mods ?: 0,
                isFavoured = favoured
            )

    override fun toString(): String {
        val result = StringBuilder()
        result.append(generateDescription(this))
        val title = name
        val isFav = if (isFavoured) "Yes" else "No"
        result.append(String.format(" (Title: \"%1\$s\", favoured: %2\$s)", title, isFav))
        return result.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Keystroke

        if (id != other.id) return false
        if (keyCode != other.keyCode) return false
        if (mods != other.mods) return false
        if (name != other.name) return false
        if (isFavoured != other.isFavoured) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + keyCode
        result = 31 * result + mods
        result = 31 * result + name.hashCode()
        result = 31 * result + isFavoured.hashCode()
        return result
    }

    companion object {
        const val TABLE_NAME = "keystroke"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_KEY_CODE = "key_code"
        const val COLUMN_MODS = "mods"
        const val COLUMN_NAME = "name"
        const val COLUMN_FAVOURED = "favoured"
    }
}