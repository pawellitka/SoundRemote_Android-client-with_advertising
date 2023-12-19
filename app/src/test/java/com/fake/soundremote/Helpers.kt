package com.fake.soundremote

import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.util.KeyCode

fun createKeystrokeWithMods(mods: Int?): Keystroke {
    return Keystroke(KeyCode(0x2D), "Name", mods, false)
}

/**
 * Creates a [Keystroke] with the specified parameters. Other parameters will have arbitrary default
 * values.
 */
fun getKeystroke(
    id: Int = 0,
    keyCode: KeyCode = KeyCode(0x42),
    mods: Int = 0,
    name: String = "Keystroke name",
    favoured: Boolean = false,
    order: Int = 0,
): Keystroke {
    return Keystroke(id, keyCode, mods, name, favoured, order)
}
