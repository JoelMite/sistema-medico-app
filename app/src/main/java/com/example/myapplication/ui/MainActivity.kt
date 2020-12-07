package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.example.myapplication.PreferenceHelper.get
import com.example.myapplication.PreferenceHelper.set
import com.example.myapplication.R
import com.example.myapplication.io.ApiService
import com.example.myapplication.io.response.LoginResponse
import com.example.myapplication.util.toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    // Variable Durmiente
    private val snackBar by lazy{
        Snackbar.make(mainLayout, R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Preferencia de datos
        // shared preferences cuando queremos guardar datos puntuales
        // sqlite cuando queremos guardar registros traidas de una base de datos que muy rara vez cambian
        // files cuando queremos guardar una imagen o archivos

        /*
        val preferences =  getSharedPreferences("general", Context.MODE_PRIVATE)
        val session = preferences.getBoolean("session", false)
        */

        val preferences = PreferenceHelper.defaultPrefs(this)
        if (preferences["access_token", ""].contains(".")) // Si el token jwt al menos existe un punto pasara a la actividad menu, sino quiere decir que es cadena vacia y por lo tanto no va a pasar
            goToMenuActivity()

        btnLogin.setOnClickListener {
            // validates
            performLogin()
        }

        tvGoToRegister.setOnClickListener {
            Toast.makeText(this, getString(R.string.please_fill_your_register_data), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isEmpty() || password.trim().isEmpty()){
            toast(getString(R.string.error_empty_credentials))
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.access_token)
                        toast("Bienvenido")
                        goToMenuActivity()
                    }else{
                        toast(getString(R.string.error_invalid_credentials))
                    }
                } else{
                    toast(getString(R.string.error_login_response))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }
        })
    }

    private fun createSessionPreference(access_token: String){
        /*
        val preferences =  getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true)
        editor.apply()
         */

        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["access_token"] = access_token
    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}