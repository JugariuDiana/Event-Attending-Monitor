package com.example.kotlin.account.Implementation

import com.example.kotlin.ATTENDEES_COLLECTION
import com.example.kotlin.EVENTS_COLLECTION
import com.example.kotlin.USERS_COLLECTION
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.StorageService
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val auth: AccountService) : StorageService {
    val database = FirebaseDatabase.getInstance("https://license-b0ebe-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
    var eventsLocation = database.child(EVENTS_COLLECTION)

    override val events: Flow<List<Event>>
        get() = observeAllEvents()

    private fun observeAllEvents(): Flow<List<Event>> {
        return callbackFlow {
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                    trySend(events).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            eventsLocation.addValueEventListener(eventListener)

            awaitClose {
                eventsLocation.removeEventListener(eventListener)
            }
        }
    }

    override val attendees: Flow<List<Attendee>>
        get() = observeAllAttendees()

    private fun observeAllAttendees(): Flow<List<Attendee>> {
        return callbackFlow {
            val attendeeListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendees = snapshot.children.mapNotNull { it.getValue(Attendee::class.java) }
                    trySend(attendees).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            database.child(ATTENDEES_COLLECTION).addValueEventListener(attendeeListener)

            awaitClose {
                database.child(ATTENDEES_COLLECTION).removeEventListener(attendeeListener)
            }
        }
    }

    override val users: Flow<List<User>>
        get() = observeAllUsers()

    private fun observeAllUsers(): Flow<List<User>> {
        return callbackFlow {
            val userListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                    trySend(users).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            database.child(USERS_COLLECTION).addValueEventListener(userListener)

            awaitClose {
                database.child(USERS_COLLECTION).removeEventListener(userListener)
            }
        }
    }

    override suspend fun createAttendee(attendee: Attendee) {
        database.child(ATTENDEES_COLLECTION).child(attendee.id).setValue(attendee).await()
    }

    override suspend fun createEvent(event: Event) {
        val eventId = generateEventId()
        val finalEvent = event.copy(id = eventId, organizerId = auth.currentUserId)
        val attendee = Attendee(id = UUID.randomUUID().toString(), userId = auth.currentUserId)
        createAttendee(attendee)
        finalEvent.attendeesList.add(attendee.id)
        eventsLocation.child(eventId).setValue(finalEvent).await()
    }

    override suspend fun readEvent(eventId: String): Event? {
        return eventsLocation.child(eventId).get().await().getValue(Event::class.java)
    }

    override suspend fun updateEvent(event: Event) {
        eventsLocation.child(event.id).setValue(event).await()
    }

    override suspend fun updateAttendee(attendee: Attendee) {
        database.child(ATTENDEES_COLLECTION).child(attendee.id).setValue(attendee).await()
    }

    override suspend fun deleteEvent(eventId: String) {
        eventsLocation.child(eventId).removeValue().await()
    }

    override suspend fun deleteAttendee(attendeeId: String) {
        database.child(ATTENDEES_COLLECTION).child(attendeeId).removeValue().await()
    }

    private fun generateEventId(): String {
        return UUID.randomUUID().toString()
    }
}