package com.example.kotlin.domain

import java.time.LocalTime

data class Attendee (
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val firstTimeSeen: LocalTime? = null,
    val lastTimeSeen: LocalTime? = null,
    val attendees: List<Int> = emptyList()
)
