package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.PreferenceHelper
import com.example.myapplication.PreferenceHelper.get
import com.example.myapplication.R
import com.example.myapplication.io.ApiService
import com.example.myapplication.model.Appointment
import com.example.myapplication.util.toast
import kotlinx.android.synthetic.main.activity_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class AppointmentsActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        loadAppointments()

//        val appointments = ArrayList<Appointment>()
//        appointments.add(Appointment(1, "Médico Test", "11/12/2020", "3:00 PM"))
//        appointments.add(Appointment(2, "Médico U", "24/12/2020", "5:00 PM"))
//        appointments.add(Appointment(3, "Médico LOP", "01/08/2020", "4:00 PM"))

        rvAppointments.layoutManager = LinearLayoutManager(this)  // Asignacion de un layoutManager para nuestro recyclerView
        rvAppointments.adapter = appointmentAdapter // Asignacion de un appointment adapter hacia nuestro recyclerView

    }

    private fun loadAppointments(){
        val access_token = preferences["access_token", ""]
        val call = apiService.getAppointments("Bearer $access_token")
        call.enqueue(object: Callback<ArrayList<Appointment>>{
            override fun onResponse(
                call: Call<ArrayList<Appointment>>,
                response: Response<ArrayList<Appointment>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { // Si no es not null pasa pero si es null no pasa
                        appointmentAdapter.appointments = it
                        appointmentAdapter.notifyDataSetChanged() // Notificar a la interfaz de que a habido cambios en el datasheet y por ende se deben pintar en la interfaz
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }
        })
    }
}