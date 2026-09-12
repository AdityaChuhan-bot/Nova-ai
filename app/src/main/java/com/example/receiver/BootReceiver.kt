package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.preferences.JarvisPreferences
import com.example.service.JarvisForegroundService

class BootReceiver : BroadcastReceiver {
    // Zero-argument constructor required for AndroidManifest receiver instantiation
    constructor() : super()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = JarvisPreferences(context).loadSettings()
            if (prefs.startOnBoot) {
                JarvisForegroundService.start(context)
            }
        }
    }
}
