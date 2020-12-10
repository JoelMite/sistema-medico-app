package com.example.myapplication.io

import com.example.myapplication.io.response.LoginResponse
import com.example.myapplication.io.response.SimpleResponse
import com.example.myapplication.model.Appointment
import com.example.myapplication.model.Doctor
import com.example.myapplication.model.Person
import com.example.myapplication.model.Schedule
import com.example.myapplication.model.Specialty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("specialties")
     fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
     fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

    @GET("schedule/hours")
     fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String): Call<Schedule>

    @POST("login")
    fun postLogin(@Query("email") email: String, @Query("password") password: String): Call<LoginResponse>

    @POST("logout")
    fun postLogout(@Header("Authorization") authHeader: String): Call<Void>

    @GET("appointments")
    fun getAppointments(@Header("Authorization") authHeader: String): Call<ArrayList<Appointment>>

    @POST("appointments")
    @Headers("Accept: application/json")
    fun storeAppointments(@Header("Authorization") authHeader: String,
                          @Query("description") description: String,
                          @Query("specialty_id") specialtyId: Int,
                          @Query("doctor_id") doctorId: Int,
                          @Query("schedule_date") scheduleDate: String,
                          @Query("schedule_time") scheduleTime: String,
                          @Query("type") type: String
                         ): Call<SimpleResponse>

    //Object Declaration -- Esta al interior de una interface en este caso
    companion object Factory{
        private const val BASE_URL = "http://192.168.0.111:8000/api/"

        fun create(): ApiService{
            val interceptor = HttpLoggingInterceptor() // Tenemos un interceptor
            interceptor.level = HttpLoggingInterceptor.Level.BODY // Definimos el level, en este caso queremos ver el cuerpo del request
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

}