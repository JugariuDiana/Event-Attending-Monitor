package com.example.kotlin.screens.event

import com.example.kotlin.AppViewModel
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.storage.AccountService
import com.example.kotlin.storage.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EndEventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    val event = MutableStateFlow(DEFAULT_EVENT)
    var userInformation = MutableStateFlow(User())
    val attendees = MutableStateFlow(emptyList<Attendee>())
    val attendeesDetected = MutableStateFlow(emptyList<Attendee>())

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = storageService.readEvent(eventId)!!
            userInformation.value = accountService.getUser(accountService.currentUserId)!!
            val organizerId = event.value.organizerId
            attendees.value = storageService.getEventAttendees(event.value.attendeesList)
            attendeesDetected.value = attendees.value.filter { it.userId != organizerId }
        }
        observeAuthenticationState(restartApp)
    }

    suspend fun getAttendeeName(userId: String): String {
        val user = storageService.getUser(userId) ?: return "can not load name"
        return user.name
    }

    private fun observeAuthenticationState(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }

    companion object {
        private val DEFAULT_EVENT = Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID)
    }
}