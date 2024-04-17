package com.example.kotlin.domain

import java.time.LocalTime

class Event (
    val id: String = "",
    val organizerId: String = "",
    val name: String = "",
    val location: String = "",
    val availableSeats: Int = 0,
    val reservedSeats: Int = 0,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val attendeesList: MutableList<Attendee> = mutableListOf(),
)