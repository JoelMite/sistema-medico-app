package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.myapplication.R
import com.example.myapplication.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    var appointments = ArrayList<Appointment>()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(appointment: Appointment){

            with(itemView) {
                val status: String = appointment.status
                val colorValue: Int
                colorValue = when(status){
                    "Reservada" -> ContextCompat.getColor(context, R.color.blue)
                    "Cancelada" -> ContextCompat.getColor(context, R.color.red)
                    "Confirmada" -> ContextCompat.getColor(context, R.color.green)
                    else -> ContextCompat.getColor(context, R.color.white)
                }
                indicator_appointment_status.setBackgroundColor(colorValue)

                tvAppointmentId.text = context.getString(
                    R.string.item_appointment_id,
                    appointment.id
                )
                tvDoctorName.text = appointment.doctor.person.name
                tvScheduledDate.text = context.getString(
                    R.string.item_appointment_date,
                    appointment.scheduledDate
                )
                tvScheduledTime.text = context.getString(
                    R.string.item_appointment_time,
                    appointment.scheduledTime
                )

                tvSpecialty.text = appointment.specialty.name
                tvDescription.text = appointment.description
                tvStatus.text = appointment.status
                tvType.text = appointment.type
                tvCreatedAt.text = context.getString(
                    R.string.item_appointment_created_at,
                    appointment.createdAt
                )

                ibExpand.setOnClickListener{
                    TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                    if (linearLayoutDetails.visibility == View.VISIBLE){
                        linearLayoutDetails.visibility = View.GONE
                        ibExpand.setImageResource(R.drawable.ic_expand_more)
                    }else{
                        linearLayoutDetails.visibility = View.VISIBLE
                        ibExpand.setImageResource(R.drawable.ic_expand_less)
                    }
                }
            }
        }

    }
    // Inflar la vista a partir del XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
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