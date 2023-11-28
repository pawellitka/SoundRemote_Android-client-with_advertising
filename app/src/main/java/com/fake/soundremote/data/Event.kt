package com.fake.soundremote.data

import com.fake.soundremote.R
import com.fake.soundremote.util.AppPermission

internal enum class Event(
    val id: Int,
    val nameStringId: Int,
    val requiredPermission: AppPermission?,
    // Min SDK version for permission to be requested for the event.
    // If == null permission will always be requested.
    val permissionMinSdk: Int?,
) {
    CALL_BEGIN(1, R.string.event_name_call_start, AppPermission.Phone, 31),
    CALL_END(2, R.string.event_name_call_end, AppPermission.Phone, 31);
}