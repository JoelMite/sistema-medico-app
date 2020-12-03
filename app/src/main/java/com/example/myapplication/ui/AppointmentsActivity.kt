package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*
import java.util.ArrayList

class AppointmentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()
        appointments.add(Appointment(1, "Médico Test", "11/12/2020", "3:00 PM"))
        appointments.add(Appointment(2, "Médico U", "24/12/2020", "5:00 PM"))
        appointments.add(Appointment(3, "Médico LOP", "01/08/2020", "4:00 PM"))

        rvAppointments.layoutManager = LinearLayoutManager(this)
        rvAppointments.adapter = AppointmentAdapter(appointments)

    }
}