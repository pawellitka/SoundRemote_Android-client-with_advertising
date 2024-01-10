package com.fake.soundremote.data

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.room.ColumnInfo
import androidx.room.Ignore

data class Action(
    @ColumnInfo(name = COLUMN_TYPE, defaultValue = "0")
    var actionType: Int,

    @ColumnInfo(name = COLUMN_ID)
    var actionId: Int,
) {
    @Ignore
    constructor(actionType: ActionType, actionId: Int) : this(actionType.id, actionId)

    companion object {
        const val COLUMN_TYPE = "action_type"
        const val COLUMN_ID = "action_id"
    }
}

data class ActionState(val type: ActionType, val id: Int) {
    companion object {
        /**
         * [Saver] implementation for [ActionState].
         */
        val saver = object : Saver<ActionState?, IntArray> {
            override fun restore(value: IntArray): ActionState? {
                if (value.isEmpty()) return null
                return ActionState(ActionType.getById(value[0]), value[1])
            }

            override fun SaverScope.save(value: ActionState?): IntArray {
                if (value == null) return intArrayOf()
                return intArrayOf(value.type.id, value.id)
            }
        }
    }
}