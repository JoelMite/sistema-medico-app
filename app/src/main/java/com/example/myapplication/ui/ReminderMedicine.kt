package com.example.myapplication.ui

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.data.ReminderData
import com.example.myapplication.notif.NotificationHelper

class ReminderMedicine : Application() {

    companion object {
        lateinit var instance: ReminderMedicine
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_LOW, true,
            ReminderData.GenderType.Woman.name, "Notification channel for women.")
        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_HIGH, true,
            ReminderData.GenderType.Man.name, "Notification channel for mens.")
//        NotificationHelper.createNotificationChannel(this,
//            NotificationManagerCompat.IMPORTANCE_NONE, false,
//            ReminderData.GenderType.Other.name, "Notification channel for other patients.")
    }
}