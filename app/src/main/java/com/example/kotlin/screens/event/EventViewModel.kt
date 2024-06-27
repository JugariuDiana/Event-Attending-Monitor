package com.example.kotlin.screens.event

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    val event = MutableStateFlow(DEFAULT_EVENT)
    var userInformation = MutableStateFlow(User())
    var name = MutableStateFlow("")
    var location = MutableStateFlow("")
    var availableSeats = MutableStateFlow("")
    var startTime = MutableStateFlow("0:0")
    var endTime = MutableStateFlow("0:0")
    var date = MutableStateFlow("")
    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = storageService.readEvent(eventId)!!
            name.value = event.value.name
            location.value = event.value.location
            availableSeats.value = event.value.availableSeats.toString()
            startTime.value = event.value.startTime
            endTime.value = event.value.endTime
            date.value = event.value.date
            userInformation.value = accountService.getUser(accountService.currentUserId)!!
        }
        observeAuthenticationState(restartApp)
    }

    private fun observeAuthenticationState(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }

    fun updateName(new: String) {
        name.value = new
        event.value.name = new
    }

    fun updateLocation(new: String) {
        location.value = new
        event.value.location = new
    }

    fun updateAvailableSeats(new: Int) {
//        availableSeats.value = new ?: ""
        event.value.availableSeats = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStartTime(new: String) {
        startTime.value = new
        event.value.startTime = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEndTime(new: String) {
        endTime.value = new
        event.value.endTime = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(new: String) {
        date.value = new
        event.value.date = new
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
        launchCatching {
            val attendee = storageService.getUsersAttendance(userInformation.value.id, event.value.attendeesList)
            event.value.attendeesList.remove(attendee)
            event.value.reservedSeats--
            storageService.updateEvent(event.value)
            storageService.deleteAttendee(attendee)
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
            for (attendeeId in event.value.attendeesList)
                storageService.deleteAttendee(attendeeId)
            storageService.deleteEvent(event.value.id)
        }

        popUpScreen()
    }

    companion object {
        private val DEFAULT_EVENT = Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID)
    }
}