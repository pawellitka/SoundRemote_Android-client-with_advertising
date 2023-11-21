package com.fake.soundremote.ui

import com.fake.soundremote.MainDispatcherExtension
import com.fake.soundremote.data.preferences.TestPreferencesRepository
import com.fake.soundremote.ui.settings.SettingsViewModel
import com.fake.soundremote.util.DEFAULT_AUDIO_COMPRESSION
import com.fake.soundremote.util.DEFAULT_CLIENT_PORT
import com.fake.soundremote.util.DEFAULT_SERVER_PORT
import com.fake.soundremote.util.Net.COMPRESSION_320
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class SettingsViewModelTest {
    private val preferencesRepository = TestPreferencesRepository()

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setup() {
        viewModel = SettingsViewModel(preferencesRepository)
    }

    @Test
    fun audioCompression_changes_settingsStateUpdates() = runTest {
        assertEquals(DEFAULT_AUDIO_COMPRESSION, viewModel.settings.value.audioCompression)
        val expected = COMPRESSION_320

        preferencesRepository.setAudioCompression(expected)

        assertEquals(expected, viewModel.settings.value.audioCompression)
    }

    @Test
    fun clientPort_changes_settingsStateUpdates() = runTest {
        assertEquals(DEFAULT_CLIENT_PORT, viewModel.settings.value.clientPort)
        val expected = 33333

        preferencesRepository.setClientPort(expected)

        assertEquals(expected, viewModel.settings.value.clientPort)
    }

    @Test
    fun serverPort_changes_settingsStateUpdates() = runTest {
        assertEquals(DEFAULT_SERVER_PORT, viewModel.settings.value.serverPort)
        val expected = 44444

        preferencesRepository.setServerPort(expected)

        assertEquals(expected, viewModel.settings.value.serverPort)
    }
}
