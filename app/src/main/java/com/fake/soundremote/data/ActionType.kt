package com.fake.soundremote.data

import androidx.annotation.StringRes
import com.fake.soundremote.R

internal enum class ActionType(
    val id: Int,
    @StringRes
    val nameStringId: Int,
) {
    APP(1, R.string.action_type_app),
    KEYSTROKE(2, R.string.action_type_keystroke),
}
