package com.example.kotlin.domain

import java.time.LocalTime

data class Attendee (
    val id: Int? = null,
    val userId: Int? = null,
    val eventId: Int? = null,
    val firstTimeSeen: LocalTime? = null,
    val lastTimeSeen: LocalTime? = null,
    val attendees: List<Int> = emptyList()
)
