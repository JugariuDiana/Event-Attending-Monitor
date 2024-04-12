package com.example.kotlin.domain

data class User (
    val id: Int? = null,
    val firstName: String = "",
    val lastName: String = "",
//    val dateOfBirth: String = "",
    val email: String = "",
    val attendeesList: MutableList<Event> = mutableListOf(),
)