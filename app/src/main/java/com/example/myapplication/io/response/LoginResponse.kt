package com.example.myapplication.io.response

import com.example.myapplication.model.User

data class LoginResponse (val success: Boolean, val user: User, val access_token: String)