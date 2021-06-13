package com.example.myapplication.ui

import android.content.Context
import com.example.myapplication.data.DataUtils
import com.example.myapplication.data.ReminderData
import com.example.myapplication.io.MainContracts

class MainPresenter (
    private val view: MainContracts.View,
    private val mainModel: MainModel
) : MainContracts.Presenter {
    override fun start() {
        val reminderDataList = mainModel.loadAllReminders()
        if (!reminderDataList.isNullOrEmpty()) {
            view.displayReminders(reminderDataList)
        } else {
            view.displayEmptyState()
        }
    }

    override fun loadSampleData(sampleData: List<ReminderData>) {
        view.displaySampleDataInserted(mainModel.insertSampleReminders(sampleData))
        //.. call start after sample data inserted
        start()
    }

    override fun createReminder() {
        view.displayCreateReminder()
    }

    override fun editReminder(reminderData: ReminderData) {
        view.displayEditReminder(reminderData)
    }

    override fun deleteReminders() {
        mainModel.deleteAllReminders()
        val reminderDataList = mainModel.loadAllReminders()
        if (!reminderDataList.isNullOrEmpty()) {
            view.displayReminders(reminderDataList)
        } else {
            view.displayEmptyState()
        }
    }

    override fun scheduleAlarmsForSampleData(context: Context) {
        DataUtils.scheduleAlarmsForData(context)
    }

    override fun deleteAlarmsForSampleData(context: Context) {
        DataUtils.deleteAlarmsForData(context)
    }
}