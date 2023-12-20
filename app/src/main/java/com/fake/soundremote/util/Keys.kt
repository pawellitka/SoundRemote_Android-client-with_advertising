@file:JvmName("Keys")

package com.fake.soundremote.util

import androidx.annotation.StringRes
import com.fake.soundremote.R
import com.fake.soundremote.data.Keystroke

/**
 * If this Char is a digit or an english alphabet letter, returns the corresponding virtual-key code.
 * Otherwise, returns null.
 */
fun Char.toKeyCode(): KeyCode? {
    if (this in '0'..'9') {
        return KeyCode(0x30 + this.code - '0'.code)
    }
    val lowercase = this.lowercaseChar()
    return if (lowercase in 'a'..'z') {
        KeyCode(0x41 + lowercase.code - 'a'.code)
    } else null
}

/**
 * Generates a description for the keystroke, for example "Ctrl + Alt + Shift + Space"
 *
 * @param keystroke the source [Keystroke]
 * @return description
 */
fun generateDescription(keystroke: Keystroke): String =
    generateDescription(keystroke.keyCode, keystroke.mods)

/**
 * Generates a description for the keystroke, for example "Ctrl + Alt + Shift + Space"
 *
 * @param keyCode main key virtual-key code
 * @param mods mods bitfield
 * @return description
 */
fun generateDescription(keyCode: KeyCode, mods: Mods): String = ModKey.entries
    .filter { mods.isModActive(it) }
    .fold("") { result, mod -> result + "${mod.label} + " } + keyCode.keyLabel()

// https://support.microsoft.com/en-us/windows/using-your-keyboard-18b2efc1-9e32-ba5a-0896-676f9f3b994f
// https://learn.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
enum class KeyGroup(val nameStringId: Int, val label: String) {
    LETTER_DIGIT(R.string.key_group_letter_digit, "a-9"),
    MEDIA(R.string.key_group_media, "⏯"),
    TYPING(R.string.key_group_typing, "' \\"),
    CONTROL(R.string.key_group_control, "Esc"),
    NAVIGATION(R.string.key_group_navigation, "➡"),
    NUM_PAD(R.string.key_group_numpad, "*"),
    FUNCTION(R.string.key_group_function, "F1");

    val index: Int
        get() = ordinal
}

/**
 * A keyboard key.
 *
 * @param keyCode Windows Virtual-Key code
 * @param label Key description
 * @param group [KeyGroup] this Key belongs to
 * @param descriptionStringId optional description resource string id
 */
enum class Key(
    val keyCode: KeyCode,
    val label: String,
    val group: KeyGroup,
    @StringRes
    val descriptionStringId: Int? = null
) {
    // Media
    MEDIA_PLAY_PAUSE(KeyCode(0xB3), "Play/Pause Media", KeyGroup.MEDIA),
    MEDIA_NEXT(KeyCode(0xB0), "Next Track", KeyGroup.MEDIA),
    MEDIA_PREV(KeyCode(0xB1), "Previous Track", KeyGroup.MEDIA),
    MEDIA_STOP(KeyCode(0xB2), "Stop Media", KeyGroup.MEDIA),
    MEDIA_VOLUME_MUTE(KeyCode(0xAD), "Volume Mute", KeyGroup.MEDIA),
    MEDIA_VOLUME_DOWN(KeyCode(0xAE), "Volume Down", KeyGroup.MEDIA),
    MEDIA_VOLUME_UP(KeyCode(0xAF), "Volume Up", KeyGroup.MEDIA),

    // Typing
    TILDE(KeyCode(0xC0), "`", KeyGroup.TYPING, R.string.key_desc_tilde),
    MINUS(KeyCode(0xBD), "−", KeyGroup.TYPING, R.string.key_desc_minus),
    PLUS(KeyCode(0xBB), "=", KeyGroup.TYPING, R.string.key_desc_plus),
    SQUARE_BRACKET_LEFT(KeyCode(0xDB), "[", KeyGroup.TYPING, R.string.key_desc_bracket_open),
    SQUARE_BRACKET_RIGHT(KeyCode(0xDD), "]", KeyGroup.TYPING, R.string.key_desc_bracket_close),
    SEMICOLON(KeyCode(0xBA), ";", KeyGroup.TYPING, R.string.key_desc_semicolon),
    QUOTE(KeyCode(0xDE), "'", KeyGroup.TYPING, R.string.key_desc_quote),
    BACKSLASH(KeyCode(0xDC), "\\", KeyGroup.TYPING, R.string.key_desc_backslash),
    COMMA(KeyCode(0xBC), ",", KeyGroup.TYPING, R.string.key_desc_comma),
    PERIOD(KeyCode(0xBE), ".", KeyGroup.TYPING, R.string.key_desc_period),
    SLASH(KeyCode(0xBF), "/", KeyGroup.TYPING, R.string.key_desc_slash),

    // Control
    TAB(KeyCode(0x09), "Tab", KeyGroup.CONTROL),
    SPACE(KeyCode(0x20), "Space", KeyGroup.CONTROL),
    BACK(KeyCode(0x08), "Backspace", KeyGroup.CONTROL),
    ENTER(KeyCode(0x0D), "Enter", KeyGroup.CONTROL),
    CAPITAL(KeyCode(0x14), "Caps Lock", KeyGroup.CONTROL),
    ESC(KeyCode(0x1B), "Esc", KeyGroup.CONTROL),
    PRINT_SCREEN(KeyCode(0x2C), "Print Screen", KeyGroup.CONTROL),
    SCROLL_LOCK(KeyCode(0x91), "Scroll Lock", KeyGroup.CONTROL),
    PAUSE(KeyCode(0x13), "Pause", KeyGroup.CONTROL),

    // Navigation
    INSERT(KeyCode(0x2D), "Insert", KeyGroup.NAVIGATION),
    DELETE(KeyCode(0x2E), "Delete", KeyGroup.NAVIGATION),
    HOME(KeyCode(0x24), "Home", KeyGroup.NAVIGATION),
    END(KeyCode(0x23), "End", KeyGroup.NAVIGATION),
    PAGE_UP(KeyCode(0x21), "Page Up", KeyGroup.NAVIGATION),
    PAGE_DOWN(KeyCode(0x22), "Page Down", KeyGroup.NAVIGATION),
    UP(KeyCode(0x26), "Up", KeyGroup.NAVIGATION),
    LEFT(KeyCode(0x25), "Left", KeyGroup.NAVIGATION),
    RIGHT(KeyCode(0x27), "Right", KeyGroup.NAVIGATION),
    DOWN(KeyCode(0x28), "Down", KeyGroup.NAVIGATION),

    // Numpad
    NUM_0(KeyCode(0x60), "Num 0", KeyGroup.NUM_PAD),
    NUM_1(KeyCode(0x61), "Num 1", KeyGroup.NUM_PAD),
    NUM_2(KeyCode(0x62), "Num 2", KeyGroup.NUM_PAD),
    NUM_3(KeyCode(0x63), "Num 3", KeyGroup.NUM_PAD),
    NUM_4(KeyCode(0x64), "Num 4", KeyGroup.NUM_PAD),
    NUM_5(KeyCode(0x65), "Num 5", KeyGroup.NUM_PAD),
    NUM_6(KeyCode(0x66), "Num 6", KeyGroup.NUM_PAD),
    NUM_7(KeyCode(0x67), "Num 8", KeyGroup.NUM_PAD),
    NUM_9(KeyCode(0x69), "Num 9", KeyGroup.NUM_PAD),
    NUM_MULTIPLY(KeyCode(0x6A), "Num *", KeyGroup.NUM_PAD),
    NUM_ADD(KeyCode(0x6B), "Num +", KeyGroup.NUM_PAD),
    NUM_SUBTRACT(KeyCode(0x6D), "Num -", KeyGroup.NUM_PAD),
    NUM_DECIMAL(KeyCode(0x6E), "Num .", KeyGroup.NUM_PAD),
    NUM_DIVIDE(KeyCode(0x6F), "Num /", KeyGroup.NUM_PAD),
    NUM_LOCK(KeyCode(0x90), "Num Lock", KeyGroup.NUM_PAD),

    // Function
    F1(KeyCode(0x70), "F1", KeyGroup.FUNCTION),
    F2(KeyCode(0x71), "F2", KeyGroup.FUNCTION),
    F3(KeyCode(0x72), "F3", KeyGroup.FUNCTION),
    F4(KeyCode(0x73), "F4", KeyGroup.FUNCTION),
    F5(KeyCode(0x74), "F5", KeyGroup.FUNCTION),
    F6(KeyCode(0x75), "F6", KeyGroup.FUNCTION),
    F7(KeyCode(0x76), "F7", KeyGroup.FUNCTION),
    F8(KeyCode(0x77), "F8", KeyGroup.FUNCTION),
    F9(KeyCode(0x78), "F9", KeyGroup.FUNCTION),
    F10(KeyCode(0x79), "F10", KeyGroup.FUNCTION),
    F11(KeyCode(0x7A), "F11", KeyGroup.FUNCTION),
    F12(KeyCode(0x7B), "F12", KeyGroup.FUNCTION);

    override fun toString(): String {
        return label
    }
}

enum class ModKey(val bitField: Int, val label: String) {
    WIN(1, "Win"),
    CTRL(1 shl 1, "Ctrl"),
    SHIFT(1 shl 2, "Shift"),
    ALT(1 shl 3, "Alt");
}

/**
 * Checks this keystroke for the specific mod key
 *
 * @param mod [ModKey] to check
 * @return true if this keystroke has the specified mod key active
 */
fun Keystroke.isModActive(mod: ModKey): Boolean {
    return mods.isModActive(mod)
}

@JvmInline
value class KeyCode(val value: Int) {
    /**
     * If this [KeyCode] is a virtual-key code for a digit or an english alphabet letter key, returns a Char
     * representing the corresponding character. Otherwise, returns null.
     */
    fun toLetterOrDigitChar(): Char? {
        if (value in 0x30..0x39) {
            return ('0'.code + value - 0x30).toChar()
        }
        return if (value in 0x41..0x5A) {
            ('a'.code + value - 0x41).toChar()
        } else null
    }

    /**
     * Returns a text label for the virtual-key code
     *
     * @return text label
     */
    fun keyLabel(): String {
        return toLetterOrDigitChar()?.toString()?.uppercase()
            ?: Key.entries.find { it.keyCode == this }?.label
            ?: "<?>"
    }
}

@JvmInline
value class Mods(val value: Int = 0) {
    constructor(win: Boolean, ctrl: Boolean, shift: Boolean, alt: Boolean) : this(
        (if (win) ModKey.WIN.bitField else 0)
                or (if (ctrl) ModKey.CTRL.bitField else 0)
                or (if (shift) ModKey.SHIFT.bitField else 0)
                or (if (alt) ModKey.ALT.bitField else 0)
    )

    fun isModActive(mod: ModKey): Boolean {
        return value and mod.bitField != 0
    }
}
