package com.fake.soundremote.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val settings: StateFlow<SettingsUIState> =
        preferencesRepository.settingsScreenPreferencesFlow.map { prefs ->
            SettingsUIState(
                serverPort = prefs.serverPort,
                clientPort = prefs.clientPort,
                audioCompression = prefs.audioCompression,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUIState(0, 0, 0),
        )

    fun setServerPort(value: Int) {
        viewModelScope.launch { preferencesRepository.setServerPort(value) }
    }

    fun setClientPort(value: Int) {
        viewModelScope.launch { preferencesRepository.setClientPort(value) }
    }

    fun setAudioCompression(value: Int) {
        viewModelScope.launch { preferencesRepository.setAudioCompression(value) }
    }
}

data class SettingsUIState(
    val serverPort: Int,
    val clientPort: Int,
    val audioCompression: Int,
)
