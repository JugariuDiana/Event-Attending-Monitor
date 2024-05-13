package com.example.kotlin.account.Implementation

import android.util.Log
import com.example.kotlin.EVENTS_COLLECTION
import com.example.kotlin.USERS_COLLECTION
import com.example.kotlin.account.AccountService
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {
    val database = FirebaseDatabase.getInstance("https://license-b0ebe-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference()
    var usersLocation = myRef.child(USERS_COLLECTION)

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    launch {
                        val user = auth.currentUser?.let {
                            getUser(it.uid) }
                        trySend(user)
                    }
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override suspend fun getUser(userId: String): User? {
        return usersLocation.child(userId).get().await().getValue(User::class.java)
    }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        val id = currentUserId
        Firebase.auth.currentUser!!.delete().await()
        usersLocation.child(currentUserId).removeValue().await()
    }
}