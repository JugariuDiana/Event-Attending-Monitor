package com.example.kotlin.domain

import com.google.firebase.firestore.DocumentId
import java.time.LocalTime

private const val TITLE_MAX_SIZE = 60

data class Event (
    @DocumentId val id: String = "",
    val organizerId: String = "",
    val name: String = "",
    val location: String = "",
    val availableSeats: Int = 0,
    val reservedSeats: Int = 0,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val attendeesList: MutableList<Attendee> = mutableListOf(),
)

fun Event.getName(): String{
    val isLongText = this.name.length > TITLE_MAX_SIZE
    val endRange = if (isLongText) TITLE_MAX_SIZE else this.name.length - 1
    return this.name.substring(IntRange(0, endRange))
}