package com.example.kotlin.domain

data class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
//    val dateOfBirth: String = "",
    val email: String = "",
    val attendeesList: MutableList<Event> = mutableListOf(),
)