package com.fake.soundremote.ui

import com.fake.soundremote.MainDispatcherExtension
import com.fake.soundremote.data.TestKeystrokeRepository
import com.fake.soundremote.data.preferences.TestPreferencesRepository
import com.fake.soundremote.service.TestServiceManager
import com.fake.soundremote.ui.home.HomeViewModel
import com.fake.soundremote.util.ConnectionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
@DisplayName("HomeViewModel")
class HomeViewModelTest {
    private var preferencesRepository = TestPreferencesRepository()
    private var keystrokeRepository = TestKeystrokeRepository()
    private var serviceManager = TestServiceManager()
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setup() {
        viewModel = HomeViewModel(preferencesRepository, keystrokeRepository, serviceManager)
    }

    @Test
    @DisplayName("connect() with valid address changes status to CONNECTED")
    fun connect_validAddress_changesStatus() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.homeUIState.collect {}
        }

        keystrokeRepository.setKeystrokes(emptyList())

        val address = "123.45.67.89"
        viewModel.connect(address)

        val actualStatus = viewModel.homeUIState.value.connectionStatus
        assertEquals(ConnectionStatus.CONNECTED, actualStatus)

        collectJob.cancel()
    }

    @Test
    @DisplayName("connect() with invalid address changes messageState")
    fun connect_invalidAddress_updatesMessage() = runTest {
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.homeUIState.collect {}
        }

        keystrokeRepository.setKeystrokes(emptyList())
        assertNull(viewModel.messageState)

        val address = "Invalid address"
        viewModel.connect(address)

        val actualStatus = viewModel.homeUIState.value.connectionStatus
        assertEquals(ConnectionStatus.DISCONNECTED, actualStatus)
        val actualMessage = viewModel.messageState
        assertNotNull(actualMessage)

        collectJob.cancel()
    }
}
