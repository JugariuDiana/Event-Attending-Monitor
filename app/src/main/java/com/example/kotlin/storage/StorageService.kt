package com.example.kotlin.storage

import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val events: Flow<List<Event>>
    val attendees: Flow<List<Attendee>>
    val users: Flow<List<User>>

    suspend fun getUsersAttendance(userId: String, attendances: List<String>): String
    suspend fun getEventAttendees(attendances: List<String>): List<Attendee>
    suspend fun createEvent(event: Event)
    suspend fun createAttendee(attendee: Attendee)
    suspend fun readEvent(eventId: String): Event?
    suspend fun updateEvent(event: Event)
    suspend fun updateAttendee(attendee: Attendee)
    suspend fun deleteEvent(eventId: String)
    suspend fun deleteAttendee(attendeeId: String)
}