package com.fake.soundremote.service

import android.content.Context
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.SystemMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal class TestServiceManager : ServiceManager {
    private val _serviceState = MutableStateFlow(ServiceState())
    override val serviceState: StateFlow<ServiceState>
        get() = _serviceState
    override val systemMessages: ReceiveChannel<SystemMessage>
        get() = TODO("Not yet implemented")

    override fun bind(context: Context) {
        TODO("Not yet implemented")
    }

    override fun unbind(context: Context) {
        TODO("Not yet implemented")
    }

    override fun connect(address: String) {
        _serviceState.update {
            it.copy(connectionStatus = ConnectionStatus.CONNECTED)
        }
    }

    override fun disconnect() {
        _serviceState.update {
            it.copy(connectionStatus = ConnectionStatus.DISCONNECTED)
        }
    }

    override fun sendKeystroke(keystroke: Keystroke) {
        sentKeystroke = keystroke
    }

    override fun setMuted(value: Boolean) {
        _serviceState.update {
            it.copy(isMuted = value)
        }
    }

    // Test only
    fun setServiceState(state: ServiceState) {
        _serviceState.value = state
    }

    var sentKeystroke: Keystroke? = null
}
