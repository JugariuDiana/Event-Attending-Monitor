package com.example.kotlin.account

import com.example.kotlin.domain.Event
import kotlinx.coroutines.flow.Flow

interface EventStorageService {
    val userEvents: Flow<List<Event>>
    val events: Flow<List<Event>>
    suspend fun createEvent(event: Event)
    suspend fun readEvent(eventId: String): Event?
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(eventId: String)
}