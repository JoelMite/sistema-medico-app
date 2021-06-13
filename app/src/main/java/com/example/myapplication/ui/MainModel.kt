package com.example.myapplication.ui

import com.example.myapplication.data.DataUtils
import com.example.myapplication.data.ReminderContract
import com.example.myapplication.data.ReminderData
import com.example.myapplication.data.ReminderDbHelper

class MainModel (
    private val reminderDbHelper: ReminderDbHelper
) {

    fun loadAllReminders(): List<ReminderData> {
        val sqLiteDatabase = reminderDbHelper.readableDatabase
        val cursor = sqLiteDatabase.query(ReminderContract.TABLE_NAME, null, null, null, null, null, null)
        val reminders = ArrayList<ReminderData>(cursor.count)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val reminderData = DataUtils.createReminderFromCursor(cursor)
                reminders.add(reminderData)
                cursor.moveToNext()
            }
        }
        cursor.close()

        return reminders
    }

    fun insertSampleReminders(reminderDataList: List<ReminderData>): Int {
        val sqLiteDatabase = reminderDbHelper.writableDatabase
        var count = 0 // track successful insert count
        for (reminderData in reminderDataList) {
            val contentValues = DataUtils.createContentValues(reminderData)
            val rowId = sqLiteDatabase.insert(ReminderContract.TABLE_NAME, null, contentValues)
            if (rowId > -1) {
                count++
            }
        }
        return count
    }


    fun deleteAllReminders() {
        val sqLiteDatabase = reminderDbHelper.readableDatabase
        sqLiteDatabase.delete(ReminderContract.TABLE_NAME, null, null)
    }
}