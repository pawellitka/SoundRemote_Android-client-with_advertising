package com.fake.soundremote.ui.keystroke

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.data.KeystrokeRepository
import com.fake.soundremote.util.Key
import com.fake.soundremote.util.KeyGroup
import com.fake.soundremote.util.ModKey
import com.fake.soundremote.util.createMods
import com.fake.soundremote.util.generateDescription
import com.fake.soundremote.util.isModActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class KeystrokeScreenMode { CREATE, EDIT }

data class KeystrokeScreenUIState(
    val mode: KeystrokeScreenMode = KeystrokeScreenMode.CREATE,
    val name: String = "",
    val win: Boolean = false,
    val ctrl: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val keyCode: Int? = null,
    val keyGroupIndex: Int = KeyGroup.LETTER_DIGIT.index,
)

@HiltViewModel
internal class KeystrokeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val keystrokeRepository: KeystrokeRepository
) : ViewModel() {
    private var initialKeystroke: Keystroke? = null

    private val _keystrokeState: MutableStateFlow<KeystrokeScreenUIState> =
        MutableStateFlow(KeystrokeScreenUIState())
    val keystrokeScreenState: StateFlow<KeystrokeScreenUIState>
        get() = _keystrokeState

    init {
        KeystrokeEditArgs(savedStateHandle).keystrokeId?.let { id ->
            viewModelScope.launch {
                keystrokeRepository.getById(id)?.let { keystroke ->
                    initialKeystroke = keystroke
                    _keystrokeState.value = KeystrokeScreenUIState(
                        mode = KeystrokeScreenMode.EDIT,
                        name = keystroke.name,
                        win = keystroke.isModActive(ModKey.WIN),
                        ctrl = keystroke.isModActive(ModKey.CTRL),
                        shift = keystroke.isModActive(ModKey.SHIFT),
                        alt = keystroke.isModActive(ModKey.ALT),
                        keyCode = keystroke.keyCode,
                        keyGroupIndex = getKeyGroupIndex(keystroke.keyCode),
                    )
                }
            }
        }
    }

    private fun getKeyGroupIndex(keyCode: Int?): Int {
        if (keyCode == null) return KeyGroup.LETTER_DIGIT.index
        val key = Key.entries.find { it.keyCode == keyCode }
        return key?.group?.index ?: KeyGroup.LETTER_DIGIT.index
    }

    fun updateKeyCode(keyCode: Int?) {
        _keystrokeState.value = _keystrokeState.value
            .copy(keyCode = keyCode, keyGroupIndex = getKeyGroupIndex(keyCode))
    }

    fun updateName(name: String) {
        _keystrokeState.value = _keystrokeState.value.copy(name = name)
    }

    fun updateMod(mod: ModKey, value: Boolean) {
        _keystrokeState.value = when (mod) {
            ModKey.WIN -> _keystrokeState.value.copy(win = value)
            ModKey.CTRL -> _keystrokeState.value.copy(ctrl = value)
            ModKey.SHIFT -> _keystrokeState.value.copy(shift = value)
            ModKey.ALT -> _keystrokeState.value.copy(alt = value)
        }
    }

    fun canSave(): Boolean {
        return keystrokeScreenState.value.keyCode != null
    }

    fun saveKeystroke() {
        keystrokeScreenState.value.let { currentState ->
            val currentKeyCode = currentState.keyCode ?: return@let
            val mods = createMods(
                win = currentState.win,
                ctrl = currentState.ctrl,
                shift = currentState.shift,
                alt = currentState.alt,
            )
            val name = currentState.name.ifBlank {
                generateDescription(currentKeyCode, mods)
            }
            val keystrokeToUpdate = initialKeystroke
            if (keystrokeToUpdate == null) {
                val keystroke = Keystroke(currentKeyCode, name, mods)
                viewModelScope.launch {
                    keystrokeRepository.insert(keystroke)
                }
            } else {
                val id = keystrokeToUpdate.id
                val favoured = keystrokeToUpdate.isFavoured
                val order = keystrokeToUpdate.order
                val keystroke = Keystroke(id, currentKeyCode, mods, name, favoured, order)
                viewModelScope.launch {
                    keystrokeRepository.update(keystroke)
                }
            }
        }
    }
}
