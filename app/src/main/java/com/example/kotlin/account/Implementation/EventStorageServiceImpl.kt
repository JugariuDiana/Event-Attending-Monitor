package com.example.kotlin.account.Implementation

import android.util.Log
import com.example.kotlin.EVENTS_COLLECTION
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.EventStorageService
import com.example.kotlin.domain.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class EventStorageServiceImpl @Inject constructor(private val auth: AccountService) : EventStorageService {
    val database = FirebaseDatabase.getInstance("https://license-b0ebe-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference()
    var eventsLocation = myRef.child(EVENTS_COLLECTION)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val userEvents: Flow<List<Event>>
        get() =
            auth.currentUser.flatMapLatest { user ->
                observeEventsForUser(user?.id ?: "")
//                Firebase.firestore
//                    .collection(EVENTS_COLLECTION)
//                    .whereEqualTo(USER_ID_FIELD, event?.id)
//                    .dataObjects()
            }

    private fun observeEventsForUser(userId: String): Flow<List<Event>> {
        return callbackFlow {
//            val eventListener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
//                    trySend(events).isSuccess
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    close(error.toException())
//                }
//            }
//            val userEventsRef = database.orderByChild(USER_ID_FIELD).equalTo(userId)
//            userEventsRef.addValueEventListener(eventListener)
//
//            awaitClose {
//                userEventsRef.removeEventListener(eventListener)
//            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

//    @OptIn(ExperimentalCoroutinesApi::class)
//    override val events: Flow<List<Event>>
//        get() =
//            auth.currentUser.flatMapLatest { event ->
//                Firebase.firestore
//                    .collection(EVENTS_COLLECTION)
//                    .whereEqualTo(USER_ID_FIELD, event?.id)
//                    .dataObjects()
//            }

    override suspend fun createEvent(event: Event) {
        val eventId = generateEventId()
        val finalEvent = event.copy(id = eventId, organizerId = auth.currentUserId)
        finalEvent.attendeesList.add(auth.currentUserId)
        try {
            eventsLocation.child(eventId).setValue(finalEvent).await()
        } catch (e: Throwable) {
            Log.d("bleScan", e.toString())
        }
    }

    override suspend fun readEvent(eventId: String): Event? {
        val result = eventsLocation.child(eventId).get().await()
        val res = result.getValue(Event::class.java)
        return res
    }

    override suspend fun updateEvent(event: Event) {
        eventsLocation.child(event.id).setValue(event).await()
    }

    override suspend fun deleteEvent(eventId: String) {
        eventsLocation.child(eventId).removeValue().await()
    }

    private fun generateEventId(): String {
        return UUID.randomUUID().toString()
    }
}