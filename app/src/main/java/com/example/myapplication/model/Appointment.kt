package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class Appointment(
    val id: Int,
    val description: String,
    val type: String,
    val status: String,

    @SerializedName("schedule_date") val scheduledDate: String, // Se mapea los datos en base al Json que recibimos y definimos la variable que queremos
    @SerializedName("created_at_format") val createdAt: String,
    @SerializedName("schedule_time_12") val scheduledTime: String,

    val specialty: Specialty,
    val doctor: Doctor
)
