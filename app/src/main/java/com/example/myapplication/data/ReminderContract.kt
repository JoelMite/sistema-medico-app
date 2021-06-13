package com.example.myapplication.data

import android.provider.BaseColumns

object ReminderContract : BaseColumns {

    const val ID = BaseColumns._ID
    const val TABLE_NAME = "Reminders"
    const val KEY_NAME = "name"
    const val KEY_GENDER = "gender"
    const val KEY_MEDICINE = "medicine"
    const val KEY_NOTE = "note"
    const val KEY_DESC = "desc"
    const val KEY_HOUR = "hour"
    const val KEY_MINUTE = "minute"
    const val KEY_DAYS = "days"
    const val KEY_ADMINISTERED = "administered"

    internal const val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + KEY_NAME + " TEXT NOT NULL, "
            + KEY_GENDER + " TEXT NOT NULL, "
            + KEY_MEDICINE + " TEXT NOT NULL, "
            + KEY_DESC + " TEXT NOT NULL, "
            + KEY_NOTE + " TEXT, "
            + KEY_HOUR + " INTEGER, "
            + KEY_MINUTE + " INTEGER, "
            + KEY_DAYS + " TEXT NOT NULL, "
            + KEY_ADMINISTERED + " INTEGER DEFAULT 0);")
}