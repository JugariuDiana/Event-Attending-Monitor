package com.example.kotlin.account.Implementation

import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.dataObjects
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.StorageService
import com.example.kotlin.domain.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val auth: AccountService) : StorageService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val events: Flow<List<Event>>
        get() =
            auth.currentUser.flatMapLatest { event ->
                Firebase.firestore
                    .collection(EVENTS_COLLECTION)
                    .whereEqualTo(USER_ID_FIELD, event?.id)
                    .dataObjects()
            }

    override suspend fun createEvent(event: Event) {
        val noteWithUserId = event.copy(organizerId = auth.currentUserId)
        Firebase.firestore
            .collection(EVENTS_COLLECTION)
            .add(noteWithUserId).await()
    }

    override suspend fun readEvent(eventId: String): Event? {
        return Firebase.firestore
            .collection(EVENTS_COLLECTION)
            .document(eventId).get().await().toObject()
    }

    override suspend fun updateEvent(event: Event) {
        Firebase.firestore
            .collection(EVENTS_COLLECTION)
            .document(event.id).set(event).await()
    }

    override suspend fun deleteEvent(eventId: String) {
        Firebase.firestore
            .collection(EVENTS_COLLECTION)
            .document(eventId).delete().await()
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val EVENTS_COLLECTION = "event"
    }
}