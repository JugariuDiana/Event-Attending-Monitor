package com.example.kotlin.domain

import com.google.firebase.firestore.DocumentId

private const val TITLE_MAX_SIZE = 60

data class Event (
    @DocumentId var id: String = "",
    var organizerId: String = "",
    var name: String = "",
    var location: String = "",
    var availableSeats: Int = 0,
    var reservedSeats: Int = 0,
    var startTime: String = "",
    var endTime: String = "",
    var date: String = "",
    var attendeesList: MutableList<String> = mutableListOf(),
)

fun Event.getName(): String{
    val isLongText = this.name.length > TITLE_MAX_SIZE
    val endRange = if (isLongText) TITLE_MAX_SIZE else this.name.length - 1
    return this.name.substring(IntRange(0, endRange))
}