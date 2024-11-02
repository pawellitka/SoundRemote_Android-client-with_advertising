package io.github.soundremote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.content.ContextCompat
import io.github.soundremote.service.MainService
import io.github.soundremote.ui.SoundRemoteApp
import io.github.soundremote.ui.theme.SoundRemoteTheme
import io.github.soundremote.util.ACTION_CLOSE
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundRemoteTheme {
                SoundRemoteApp(calculateWindowSizeClass(this))
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
