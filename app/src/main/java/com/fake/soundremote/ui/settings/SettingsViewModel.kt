package com.fake.soundremote.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsUIState(0, 0, 0))
    val settings: StateFlow<SettingsUIState>
        get() = _settings

    init {
        viewModelScope.launch {
            preferencesRepository.settingsScreenPreferencesFlow.collect { prefs ->
                _settings.value = SettingsUIState(
                    serverPort = prefs.serverPort,
                    clientPort = prefs.clientPort,
                    audioCompression = prefs.audioCompression,
                )
            }
        }
    }

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