package com.example.kotlin.domain

data class Attendee (
    var id: String = "",
    var userId: String = "",
    var firstTimeSeen: String = "",
    var lastTimeSeen: String = "",
    var attendees: List<String> = emptyList()
)
