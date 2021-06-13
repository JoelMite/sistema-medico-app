package com.example.myapplication.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.data.DataUtils
import com.example.myapplication.notif.AlarmScheduler
import com.example.myapplication.reminder.ReminderDialog

class AppGlobalReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action != null) {

            // Handle the action to set the Medicine Administered
            if (intent.action!!.equals(context.getString(R.string.action_medicine_administered), ignoreCase = true)) {

                val extras = intent.extras
                if (extras != null) {

                    val notificationId = extras.getInt(NOTIFICATION_ID)

                    val reminderId = extras.getInt(ReminderDialog.KEY_ID)
                    val medicineAdministered = extras.getBoolean(ReminderDialog.KEY_ADMINISTERED)

                    // Lookup the reminder for sanity
                    val reminderData = DataUtils.getReminderById(reminderId)

                    if (reminderData != null) {

                        // Update the database
                        DataUtils.setMedicineAdministered(reminderId, medicineAdministered)

                        // Remove the alarm
                        AlarmScheduler.removeAlarmsForReminder(context, reminderData)
                    }

                    // finally, cancel the notification
                    if (notificationId != -1) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.cancel(notificationId)
                        notificationManager.cancelAll() // testing
                    }
                }
            }
        }
    }
}