package com.example.myapplication.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication.data.DataUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {

            // Reschedule every alarm here
            DataUtils.scheduleAlarmsForData(context)
        }
    }
}