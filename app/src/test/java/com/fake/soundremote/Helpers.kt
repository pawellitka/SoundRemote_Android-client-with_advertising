package com.fake.soundremote

import com.fake.soundremote.data.Keystroke

fun createKeystrokeWithMods(mods: Int?): Keystroke {
    return Keystroke(0x2D, "Name", mods, false)
}
