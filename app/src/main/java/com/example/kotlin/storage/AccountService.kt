package com.example.kotlin.storage

import kotlinx.coroutines.flow.Flow
import com.example.kotlin.domain.User

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
    suspend fun deleteAccount()
}