package com.fake.soundremote.data.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val settingsScreenPreferencesFlow: Flow<SettingsScreenPreferences>

    val serverAddressFlow: Flow<String>

    val audioCompressionFlow: Flow<Int>

    suspend fun setServerAddress(serverAddress: String)

    suspend fun getServerAddress(): String

    suspend fun setServerPort(value: Int)

    suspend fun getServerPort(): Int

    suspend fun setClientPort(value: Int)

    suspend fun getClientPort(): Int

    suspend fun setAudioCompression(value: Int)

    suspend fun getAudioCompression(): Int
}