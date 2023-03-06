package com.example.contactos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, NotificationService::class.java)
            context.startService(serviceIntent)
        }
    }
}