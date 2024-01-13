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
import javax.inject.Inject

data class KeystrokeInfoUIState(
    val id: Int,
    val name: String,
    val description: String,
)

@HiltViewModel
class KeystrokeSelectViewModel @Inject constructor(
    private val keystrokeRepository: KeystrokeRepository,
) : ViewModel() {
    val keystrokesState: StateFlow<List<KeystrokeInfoUIState>> = keystrokeRepository.getAllInfoOrdered()
            .map { keystrokes ->
                keystrokes.map { keystroke ->
                    KeystrokeInfoUIState(
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