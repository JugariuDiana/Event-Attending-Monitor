package com.example.kotlin.domain

import java.time.LocalTime

data class Attendee (
    var id: String = "",
    var userId: String = "",
    var eventId: String = "",
    var firstTimeSeen: LocalTime? = null,
    var lastTimeSeen: LocalTime? = null,
    var attendees: List<String> = emptyList()
)
