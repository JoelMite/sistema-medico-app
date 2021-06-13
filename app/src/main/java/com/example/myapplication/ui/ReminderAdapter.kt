package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.ReminderData
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(private val listener: Listener,
                      private val reminderDataList: List<ReminderData>?
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("h:mma", Locale.getDefault());

    interface Listener {
        fun onClick(reminderData: ReminderData)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_reminder_row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (reminderDataList != null) {
            val reminderData = reminderDataList[i]

            viewHolder.textViewName.text = reminderData.name
            viewHolder.textViewMedicine.text = reminderData.medicine
            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, reminderData.hour)
            date.set(Calendar.MINUTE, reminderData.minute)
            viewHolder.textViewTimeToAdminister.text = dateFormat.format(date.time).toLowerCase()

            var daysText = Arrays.toString(reminderData.days)
            daysText = daysText.replace("[", "")
            daysText = daysText.replace("]", "")
            daysText = daysText.replace(",", " ·")
            viewHolder.textViewDays.text = daysText

            val drawable = when(reminderData.gender) {
                ReminderData.GenderType.Man -> ContextCompat.getDrawable(viewHolder.imageViewIcon.context, R.drawable.hombre)
                ReminderData.GenderType.Woman -> ContextCompat.getDrawable(viewHolder.imageViewIcon.context, R.drawable.mujer)
//        else -> ContextCompat.getDrawable(viewHolder.imageViewIcon.context, R.drawable.other)
            }
            viewHolder.imageViewIcon.setImageDrawable(drawable)

            viewHolder.checkBoxAdministered.isChecked = reminderData.administered

            viewHolder.itemView.setOnClickListener {
                listener.onClick(reminderData)
            }

        }
    }

    override fun getItemCount(): Int {
        return reminderDataList?.size ?: 0
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageViewIcon: ImageView
        var textViewName: TextView
        var textViewMedicine: TextView
        var textViewTimeToAdminister: TextView
        var textViewDays: TextView
        var checkBoxAdministered: CheckBox

        init {

            imageViewIcon = itemView.findViewById(R.id.imageViewIcon)
            textViewName = itemView.findViewById(R.id.textViewName)
            textViewMedicine = itemView.findViewById(R.id.textViewMedicine)
            textViewTimeToAdminister = itemView.findViewById(R.id.textViewTimeToAdminister)
            textViewDays = itemView.findViewById(R.id.textViewDays)
            checkBoxAdministered = itemView.findViewById(R.id.checkBoxAdministered)
        }
    }
}