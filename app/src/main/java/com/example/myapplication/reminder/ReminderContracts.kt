package com.example.myapplication.reminder

import com.example.myapplication.data.ReminderData

interface ReminderContracts {

    interface View {

        fun displayExistingReminder(reminderData: ReminderData)

        fun displayTimeDialog(hour: Int, minute: Int)

        fun displayError(errorCode: Int)

        fun displayAdministerError()

        fun close(opCode: Int, reminderData: ReminderData)
    }

    interface Presenter {

        fun start()

        fun timeTapped()

        fun timeSelected(hourOfDay: Int, minute: Int)

        fun saveTapped(name: String, genderType: ReminderData.GenderType, medicine: String, description: String, days: Array<String?>, notes: String, administered: Boolean)

        fun deleteTapped()

        fun administeredTapped()
    }
}