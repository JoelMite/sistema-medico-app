package com.example.myapplication.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object SampleDataHelper {

    fun loadSampleData(context: Context): List<ReminderData>? {
        val inputStream = context.assets.open("rw_pet_reminder_sample.json")

        val contents = inputStream.bufferedReader().use {
            it.readText()
        }

        val listType = object : TypeToken<List<ReminderData>>() {}.type
        val gson = GsonBuilder().create()
        return gson.fromJson<List<ReminderData>>(contents, listType)

    }
}