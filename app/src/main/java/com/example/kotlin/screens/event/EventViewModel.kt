package com.example.kotlin.screens.event

import androidx.compose.runtime.collectAsState
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.StorageService
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    val event = MutableStateFlow(DEFAULT_EVENT)
    var userInformation = MutableStateFlow(User())

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = storageService.readEvent(eventId)!!
            userInformation.value = accountService.getUser(accountService.currentUserId)!!
        }
        observeAuthenticationState(restartApp)
    }

    //ToDO - see how to use isUserRegistered, complete registration, not registration and event editing
    suspend fun isUserRegistered(): Boolean {
        return storageService.attendees
            .map { attendees ->
                attendees.any { attendee ->
                    attendee.id in event.value.attendeesList && userInformation.value.id == attendee.userId
                }
            }
            .firstOrNull() ?: false
    }

    private fun observeAuthenticationState(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }

    fun updateEvent(new: Event) {
        event.value = new
    }

    fun register(popUpScreen: () -> Unit) {
        val attendee = Attendee(UUID.randomUUID().toString(), userInformation.value.id)
        event.value.attendeesList.add(attendee.id)
        event.value.reservedSeats++
        launchCatching {
            storageService.createAttendee(attendee)
            storageService.updateEvent(event.value)
        }

        popUpScreen()
    }

    fun unRegister(popUpScreen: () -> Unit) {
        val attendee = Attendee(UUID.randomUUID().toString(), userInformation.value.id)
        event.value.attendeesList.remove(attendee.id)
        event.value.reservedSeats--
        launchCatching {
            storageService.deleteAttendee(attendee.id)
            storageService.updateEvent(event.value)
        }

        popUpScreen()
    }

    fun saveEvent(popUpScreen: () -> Unit) {
        launchCatching {
            storageService.updateEvent(event.value)
        }

        popUpScreen()
    }

    fun deleteEvent(popUpScreen: () -> Unit) {
        launchCatching {
            storageService.deleteEvent(event.value.id)
        }

        popUpScreen()
    }

    companion object {
        private val DEFAULT_EVENT = Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID)
    }
}