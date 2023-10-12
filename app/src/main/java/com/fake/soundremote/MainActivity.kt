package com.fake.soundremote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.fake.soundremote.service.MainService
import com.fake.soundremote.ui.SoundRemoteApp
import com.fake.soundremote.ui.theme.SoundRemoteTheme
import com.fake.soundremote.util.ACTION_CLOSE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_CLOSE -> finishAndRemoveTask()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoundRemoteTheme {
                SoundRemoteApp()
            }
        }
        volumeControlStream = AudioManager.STREAM_MUSIC
        ContextCompat.registerReceiver(
            this,
            broadcastReceiver,
            IntentFilter(ACTION_CLOSE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        startService()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun startService() {
        val intent = Intent(this, MainService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}
