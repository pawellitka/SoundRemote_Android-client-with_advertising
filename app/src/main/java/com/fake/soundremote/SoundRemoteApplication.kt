package com.fake.soundremote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class SoundRemoteApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
}