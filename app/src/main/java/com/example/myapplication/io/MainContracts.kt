package com.example.myapplication.io

import android.content.Context
import com.example.myapplication.data.ReminderData

interface MainContracts {

    interface View {

        fun displayEmptyState()

        fun displayReminders(reminderList: List<ReminderData>)

        fun displayCreateReminder()

        fun displayEditReminder(reminderData: ReminderData)

        fun displaySampleDataInserted(count: Int)
    }

    interface Presenter {

        fun start()

        fun loadSampleData(sampleData: List<ReminderData>)

        fun createReminder()

        fun editReminder(reminderData: ReminderData)

        fun deleteReminders()

        fun scheduleAlarmsForSampleData(context: Context)

        fun deleteAlarmsForSampleData(context: Context)

    }

}