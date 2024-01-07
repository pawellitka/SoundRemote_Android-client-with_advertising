package com.fake.soundremote.data

import androidx.annotation.StringRes
import com.fake.soundremote.R
import com.fake.soundremote.util.AppPermission

internal enum class Event(
    val id: Int,
    @StringRes
    val nameStringId: Int,
    val applicableActionTypes: Set<ActionType>,
    val requiredPermission: AppPermission?,
    // Min SDK version for permission to be requested for the event.
    // If == null permission will always be requested.
    val permissionMinSdk: Int?,
) {
    CALL_BEGIN(
        1,
        R.string.event_name_call_start,
        setOf(ActionType.KEYSTROKE),
        AppPermission.Phone,
        31
    ),
    CALL_END(
        2,
        R.string.event_name_call_end,
        setOf(ActionType.KEYSTROKE),
        AppPermission.Phone,
        31
    );
}