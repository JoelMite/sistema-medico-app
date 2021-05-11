package com.example.myapplication.model

data class Doctor(
    val id: Int,
    val email: String,
    val name: String,
    val lastname: String,
    val person: Person
){
    override fun toString(): String {
        return name
    }
}
