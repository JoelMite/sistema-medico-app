package com.example.myapplication.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.data.DataUtils
import com.example.myapplication.reminder.ReminderDialog

class AlarmReceiver : BroadcastReceiver() {

    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive() called with: context = [$context], intent = [$intent]")
        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(context.getString(R.string.action_notify_administer_medication), ignoreCase = true)) {
                if (intent.extras != null) {
                    val reminderData = DataUtils.getReminderById(intent.extras!!.getInt(
                        ReminderDialog.KEY_ID))
                    if (reminderData != null) {
                        NotificationHelper.createNotificationForPet(context, reminderData)
                    }
                }
            }
        }
    }
}