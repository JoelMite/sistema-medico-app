package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myapplication.PreferenceHelper
import com.example.myapplication.PreferenceHelper.set
import com.example.myapplication.PreferenceHelper.get
import com.example.myapplication.R
import com.example.myapplication.io.ApiService
import com.example.myapplication.util.toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
// import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken = intent.getBooleanExtra("store_token", false)
        if (storeToken)
            storeToken()

        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            performLogout()
//            clearSessionPreference()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }

        // Codigo Nuevo para el Reminder
        btnReminderMedicine.setOnClickListener{
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun storeToken(){
        val access_token = preferences["access_token", ""]
        val authHeader = "Bearer $access_token"

//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            // Get new FCM registration token
//            val token = task.result
//            Log.d("FCMService", token)
//        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) {instanceIdResult ->
            val deviceToken = instanceIdResult.token
            Log.d("FCMService",deviceToken)
            val call = apiService.postToken(authHeader, deviceToken)
            call.enqueue(object: Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Log.d(TAG, "Token Registrado Correctamente")
                    }else{
                        Log.d(TAG, "Hubo un problema al registrar el Token")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }
            })
        }
    }

    private fun performLogout(){
        val access_token = preferences["access_token", ""]
        val call = apiService.postLogout("Bearer $access_token")
        call.enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()

                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }
        })
    }

    private fun clearSessionPreference(){
        /*
        val preferences =  getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", false)
        editor.apply()
        */
        preferences["access_token"] = ""
    }

    companion object {
        private const val TAG = "MenuActivity"
    }
}