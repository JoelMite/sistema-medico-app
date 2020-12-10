package com.example.myapplication.ui

import com.example.myapplication.model.Doctor
import com.example.myapplication.model.Person
import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.myapplication.PreferenceHelper
import com.example.myapplication.PreferenceHelper.get
import com.example.myapplication.R
import com.example.myapplication.io.ApiService
import com.example.myapplication.io.response.SimpleResponse
import com.example.myapplication.model.Schedule
import com.example.myapplication.model.Specialty
import com.example.myapplication.util.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import retrofit2.Call
import java.util.*
// import javax.security.auth.callback.Callback
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    private val apiService: ApiService by lazy {
        ApiService.create()
    }
    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioBtn: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if (etDescription.text.toString().length < 3) {
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                // continue to step 2
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener {
            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error = getString(R.string.validate_appointment_date)
                }
                selectedTimeRadioBtn == null -> {
                    Snackbar.make(
                        createAppointmentLinearLayout,
                        R.string.validate_appointment_time, Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    // continue to step 3
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }

        }

        btnConfirmAppointment.setOnClickListener {
            performStoreAppointment()
        }

        loadSpecialties()
        listenSpecialtyChanges()
        listenDoctorAndDateChanges()

//        val doctorsOptions = arrayOf("Doctor A", "Doctor B", "Doctor C")
//        spinnerDoctors.adapter = ArrayAdapter<String>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1, doctorsOptions)

    }

    private fun performStoreAppointment() {
        btnConfirmAppointment.isClickable =
            false // Esto me permite desactivar el boton para confirmar una cita luego de que ya lo haya aplastado y evitar asi que se aplaste varias veces y cree multiples registros

        val access_token = preferences["access_token", ""]
        val authHeader = "Bearer $access_token"
        val description = tvConfirmDescription.text.toString()
        val specialty = spinnerSpecialties.selectedItem as Specialty
        val doctor = spinnerDoctors.selectedItem as Doctor
        val scheduleDate = tvConfirmDate.text.toString()
        val scheduleTime = tvConfirmTime.text.toString()
        val type = tvConfirmType.text.toString()

        val call = apiService.storeAppointments(
            authHeader,
            description,
            specialty.id,
            doctor.id,
            scheduleDate,
            scheduleTime,
            type
        )
        call.enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful) {
                    toast(getString(R.string.create_appointment_success))
                    finish()
                } else {
                    toast(getString(R.string.create_appointment_error))
                    btnConfirmAppointment.isClickable = true // En caso de que haya fallado algo, el boton estara disponible de nuevo
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                btnConfirmAppointment.isClickable = true
                toast(t.localizedMessage)
            }
        })
        
    }

    private fun listenDoctorAndDateChanges() {
        // doctors -- Esto sucede cuando cambia la seleccion del medico
        spinnerDoctors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                //Toast.makeText(this@CreateAppointmentActivity, "id: ${specialty.id}", Toast.LENGTH_SHORT).show()
                loadHours(doctor.id, etScheduledDate.text.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        // scheduled date -- Esto sucede cuando cambia la seleccion de la fecha
        etScheduledDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun loadHours(doctorId: Int, date: String) {
        // Si la fecha esta vacia no es necesario cargar las horas.
        if (date.isEmpty()) {
            return
        }
        val call = apiService.getHours(doctorId, date)
        call.enqueue(object : Callback<Schedule> {
            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if (response.isSuccessful) {
                    val schedule = response.body()
                    // Toast.makeText(this@CreateAppointmentActivity, "morning: ${schedule?.morning?.size},afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
                    schedule?.let {
                        tvSelectDoctorAndDate.visibility = View.GONE
                        val intervals = it.morning + it.afternoon
                        val hours = ArrayList<String>()
                        intervals.forEach { interval ->
                            hours.add(interval.start)
                        }
                        displayIntervalRadios(hours)
                    }
                }
            }

            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(
                    this@CreateAppointmentActivity,
                    getString(R.string.error_loading_hours),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        // Toast.makeText(this, "doctor: $doctorId, date: $date", Toast.LENGTH_SHORT).show()
    }

    private fun loadSpecialties() {
        val call = apiService.getSpecialties()
        // Object Expression
        call.enqueue(object : Callback<ArrayList<Specialty>> {
            override fun onResponse(
                call: Call<ArrayList<Specialty>>,
                response: Response<ArrayList<Specialty>>
            ) {
                if (response.isSuccessful) { // Si el codigo de respuesta es entre 200 .. 300 accedemos a la respuesta
                    val specialties = response.body() // ArrayList de Especialidades (Objetos)

//                    val specialtyOptions = ArrayList<String>() // ArrayList de Cadenas
//                    specialties?.forEach{
//                        specialtyOptions.add(it.name) // Tomamos la variable nombre de cada especialidad y guardamos en otro array en este caso specialtyOptions
//                    }

                    spinnerSpecialties.adapter = specialties?.let {
                        ArrayAdapter<Specialty>(
                            this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                            it
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(
                    this@CreateAppointmentActivity,
                    getString(R.string.error_loading_specialties),
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Finaliza el activity anterior
            }

        })

    }

    private fun listenSpecialtyChanges() {
        spinnerSpecialties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                //Toast.makeText(this@CreateAppointmentActivity, "id: ${specialty.id}", Toast.LENGTH_SHORT).show()
                loadDoctors(specialty.id)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun loadDoctors(specialtyId: Int) {
        val call = apiService.getDoctors(specialtyId)
        call.enqueue(object : Callback<ArrayList<Doctor>> {
            override fun onResponse(
                call: Call<ArrayList<Doctor>>,
                response: Response<ArrayList<Doctor>>
            ) {
                if (response.isSuccessful) { // Si el codigo de respuesta es entre 200 .. 300 accedemos a la respuesta
                    val doctors = response.body() // ArrayList de Especialidades (Objetos)

                    spinnerDoctors.adapter = doctors?.let {
                        ArrayAdapter<Doctor>(
                            this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,
                            it
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(
                    this@CreateAppointmentActivity,
                    getString(R.string.error_loading_doctors),
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Finaliza el activity anterior
            }
        })
    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpeciality.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioBtnId = radioGroupType.checkedRadioButtonId
        val selectedRadioType = radioGroupType.findViewById<RadioButton>(selectedRadioBtnId)

        tvConfirmType.text = selectedRadioType.text.toString()
        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        tvConfirmTime.text = selectedTimeRadioBtn?.text.toString()
    }

    fun onClickScheduledDate(v: View?) {
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y, m, d)
            etScheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    (m + 1).twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduledDate.error = null
        }

        //new dialog
        val datePickerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)
        val datePicker = datePickerDialog.datePicker

        // set limits
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) //+1
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 29) //+30
        datePicker.maxDate = calendar.timeInMillis

        // show dialog
        datePickerDialog.show()
    }

    private fun displayIntervalRadios(hours: ArrayList<String>) {

//        radioGroup.clearCheck()
//        radioGroup.removeAllViews()
//        radioGroup.checkedRadioButtonId
        selectedTimeRadioBtn = null
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        // Si la fecha esta vacia no es necesario cargar las horas.
        if (hours.isEmpty()) {
            tvNoAvailableHours.visibility = View.VISIBLE
            return
        }

        tvNoAvailableHours.visibility = View.GONE

        // val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioBtn?.isChecked = false
                selectedTimeRadioBtn = view as RadioButton?
                selectedTimeRadioBtn?.isChecked = true
            }

            if (goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft

        }
    }

    private fun Int.twoDigits() = if (this >= 10) this.toString() else "0$this"

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE

            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE

            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_create_appointment_exit_title))
                builder.setMessage(getString(R.string.dialog_create_appointment_exit_title_message))
                builder.setPositiveButton(getString(R.string.dialog_create_appointment_exit_positive_button)) { _, _ ->
                    finish()
                }
                builder.setNegativeButton(getString(R.string.dialog_create_appointment_exit_negative_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

    }
}

