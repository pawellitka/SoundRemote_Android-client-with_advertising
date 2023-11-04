package com.fake.soundremote.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fake.soundremote.util.DEFAULT_AUDIO_COMPRESSION
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_ADDRESS
import com.fake.soundremote.util.DEFAULT_SERVER_PORT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class SettingsScreenPreferences(
    val serverPort: Int,
    val clientPort: Int,
    val audioCompression: Int,
)

private const val KEY_SERVER_PORT = "server_port"
private const val KEY_CLIENT_PORT = "client_port"
private const val KEY_SERVER_ADDRESS = "server_address"
private const val KEY_AUDIO_COMPRESSION = "audio_compression"

@Singleton
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    private val defaultDispatcher: CoroutineDispatcher
) {
    @Inject
    constructor(dataStore: DataStore<Preferences>) : this(dataStore, Dispatchers.IO)

    private object PreferencesKeys {
        val SERVER_ADDRESS = stringPreferencesKey(KEY_SERVER_ADDRESS)
        val SERVER_PORT = intPreferencesKey(KEY_SERVER_PORT)
        val CLIENT_PORT = intPreferencesKey(KEY_CLIENT_PORT)
        val AUDIO_COMPRESSION = intPreferencesKey(KEY_AUDIO_COMPRESSION)
    }

    private val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    val settingsScreenPreferencesFlow: Flow<SettingsScreenPreferences> = preferencesFlow
        .map { preferences ->
            val serverPort = preferences[PreferencesKeys.SERVER_PORT] ?: DEFAULT_SERVER_PORT
            val clientPort = preferences[PreferencesKeys.CLIENT_PORT] ?: DEFAULT_CLIENT_PORT
            val audioCompression =
                preferences[PreferencesKeys.AUDIO_COMPRESSION] ?: DEFAULT_AUDIO_COMPRESSION
            SettingsScreenPreferences(serverPort, clientPort, audioCompression)
        }

    val serverAddressFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[PreferencesKeys.SERVER_ADDRESS] ?: DEFAULT_SERVER_ADDRESS
        }

    val audioCompressionFlow: Flow<Int> = preferencesFlow
        .map { preferences ->
            preferences[PreferencesKeys.AUDIO_COMPRESSION] ?: DEFAULT_AUDIO_COMPRESSION
        }

    suspend fun setServerAddress(serverAddress: String) = withContext(defaultDispatcher) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.SERVER_ADDRESS] = serverAddress
        }
    }

    suspend fun getServerAddress(): String = withContext(defaultDispatcher) {
        preferencesFlow.map { preferences ->
            preferences[PreferencesKeys.SERVER_ADDRESS] ?: DEFAULT_SERVER_ADDRESS
        }.first()
    }

    suspend fun setServerPort(value: Int) = withContext(defaultDispatcher) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(KEY_SERVER_PORT)] = value
        }
    }

    suspend fun getServerPort(): Int = withContext(defaultDispatcher) {
        settingsScreenPreferencesFlow.first().serverPort
    }

    suspend fun setClientPort(value: Int) = withContext(defaultDispatcher) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(KEY_CLIENT_PORT)] = value
        }
    }

    suspend fun getClientPort(): Int = withContext(defaultDispatcher) {
        settingsScreenPreferencesFlow.first().clientPort
    }

    suspend fun setAudioCompression(value: Int) = withContext(defaultDispatcher) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(KEY_AUDIO_COMPRESSION)] = value
        }
    }

    suspend fun getAudioCompression(): Int = withContext(defaultDispatcher) {
        audioCompressionFlow.first()
    }
}
