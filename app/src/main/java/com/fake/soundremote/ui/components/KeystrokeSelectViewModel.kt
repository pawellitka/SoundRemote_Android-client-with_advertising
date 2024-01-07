package com.fake.soundremote.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.KeystrokeRepository
import com.fake.soundremote.util.generateDescription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class KeystrokeSelectUIState(
    val id: Int,
    val name: String,
    val description: String,
)

@HiltViewModel
class KeystrokeSelectViewModel(
    private val keystrokeRepository: KeystrokeRepository,
) : ViewModel() {
    val keystrokesState: StateFlow<List<KeystrokeSelectUIState>> = keystrokeRepository.getAllInfoOrdered()
            .map { keystrokes ->
                keystrokes.map { keystroke ->
                    KeystrokeSelectUIState(
                        keystroke.id,
                        keystroke.name,
                        description = generateDescription(keystroke),
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}