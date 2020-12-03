package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(private val appointments: ArrayList<Appointment>) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(appointment: Appointment){

            with(itemView) {

                tvAppointmentId.text = context.getString(R.string.item_appointment_id, appointment.id)
                tvDoctorName.text = appointment.doctorName
                tvScheduledDate.text = context.getString(R.string.item_appointment_date, appointment.scheduledDate)
                tvScheduledTime.text = context.getString(R.string.item_appointment_time, appointment.scheduledTime)
            }
        }

    }
    // Inflar la vista a partir del XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false ))
    }
    //Enlazar la data con la vista que inflamos
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appointment = appointments[position]

        holder.bind(appointment)
    }
    // Retorna la cantidad de elementos
    override fun getItemCount(): Int {
        return appointments.size
    }
}