package com.fake.soundremote.service

import android.content.Context
import com.fake.soundremote.data.Keystroke
import com.fake.soundremote.util.ConnectionStatus
import com.fake.soundremote.util.SystemMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow

internal interface ServiceManager {
    val serviceState: StateFlow<ServiceState>
    val systemMessages: ReceiveChannel<SystemMessage>
    fun bind(context: Context)
    fun unbind(context: Context)
    fun connect(address: String)
    fun disconnect()
    fun sendKeystroke(keystroke: Keystroke)
    fun setMuted(value: Boolean)
}

data class ServiceState(
    val connectionStatus: ConnectionStatus,
    val isMuted: Boolean
)
