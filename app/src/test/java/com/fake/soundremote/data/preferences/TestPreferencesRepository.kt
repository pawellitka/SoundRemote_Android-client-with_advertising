package com.fake.soundremote.data.preferences

import com.fake.soundremote.util.DEFAULT_AUDIO_COMPRESSION
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_ADDRESS
import com.fake.soundremote.util.DEFAULT_SERVER_PORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestPreferencesRepository : PreferencesRepository {
    private val _settingsScreenPreferencesFlow = MutableStateFlow(
        SettingsScreenPreferences(
            DEFAULT_SERVER_PORT,
            DEFAULT_CLIENT_PORT,
            DEFAULT_AUDIO_COMPRESSION
        )
    )
    override val settingsScreenPreferencesFlow: Flow<SettingsScreenPreferences>
        get() = _settingsScreenPreferencesFlow

    private val _serverAddressFlow = MutableStateFlow(DEFAULT_SERVER_ADDRESS)
    override val serverAddressFlow: Flow<String>
        get() = _serverAddressFlow

    private val _audioCompressionFlow = MutableStateFlow(DEFAULT_AUDIO_COMPRESSION)
    override val audioCompressionFlow: Flow<Int>
        get() = _audioCompressionFlow

    override suspend fun setServerAddress(serverAddress: String) {
        _serverAddressFlow.value = serverAddress
    }

    override suspend fun getServerAddress(): String {
        return _serverAddressFlow.value
    }

    override suspend fun setServerPort(value: Int) {
        _settingsScreenPreferencesFlow.value =
            _settingsScreenPreferencesFlow.value.copy(serverPort = value)
    }

    override suspend fun getServerPort(): Int {
        return _settingsScreenPreferencesFlow.value.serverPort
    }

    override suspend fun setClientPort(value: Int) {
        _settingsScreenPreferencesFlow.value =
            _settingsScreenPreferencesFlow.value.copy(clientPort = value)
    }

    override suspend fun getClientPort(): Int {
        return _settingsScreenPreferencesFlow.value.clientPort
    }

    override suspend fun setAudioCompression(value: Int) {
        _settingsScreenPreferencesFlow.value =
            _settingsScreenPreferencesFlow.value.copy(audioCompression = value)
        _audioCompressionFlow.value = value
    }

    override suspend fun getAudioCompression(): Int {
        return _audioCompressionFlow.value
    }
}
