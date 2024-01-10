package com.fake.soundremote.data

import androidx.annotation.StringRes
import com.fake.soundremote.R

enum class ActionType(
    val id: Int,
    @StringRes
    val nameStringId: Int,
) {
    NONE(0, R.string.action_type_none),
    APP(1, R.string.action_type_app),
    KEYSTROKE(2, R.string.action_type_keystroke);

    companion object {
        /**
         * Get [ActionType] by its id.
         * @throws [NoSuchElementException] if no entry with such id is found.
         */
        fun getById(id: Int): ActionType {
            return ActionType.entries.first { it.id == id }
        }
    }
}
