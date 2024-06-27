package com.example.kotlin.screens.eventList

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlin.ADD_EVENT_SCREEN
import com.example.kotlin.BLE_ACTIVITY
import com.example.kotlin.EVENT_SCREEN
import com.example.kotlin.REGISTER_EVENT_SCREEN
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.UNREGISTER_EVENT_SCREEN
import com.example.kotlin.storage.AccountService
import com.example.kotlin.storage.StorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.AppViewModel
import com.example.kotlin.END_EVENT_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    val events = storageService.events
    val user = accountService.currentUser

    fun initialize(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }
    fun onAddClick(openScreen: (String) -> Unit) {
        openScreen("$ADD_EVENT_SCREEN?")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun onEventClick(context: Context, openScreen: (String) -> Unit, event: Event, user: User) {
        if (eventEnded(event)){
            openScreen("$END_EVENT_SCREEN/${event.id}")
            return
        }

        if (event.organizerId == user.id && !eventIsRunning(event)){
            openScreen("$EVENT_SCREEN/${event.id}")
            return
        }

        launchCatching {
            val attendees = storageService.getEventAttendees(event.attendeesList)
            for (attendance in attendees)
                if (attendance.userId == user.id) {
                    if (eventIsRunning(event)){
                        openScreen("$BLE_ACTIVITY/${event.id}")
                        return@launchCatching
                    }
                    openScreen("$UNREGISTER_EVENT_SCREEN/${event.id}")
                    return@launchCatching
                }
            openScreen("$REGISTER_EVENT_SCREEN/${event.id}")
        }
    }

    fun onSignOutClick() {
        launchCatching {
            accountService.signOut()
        }
    }

    fun onDeleteAccountClick() {
        launchCatching {
            accountService.deleteAccount()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun eventIsRunning(event: Event): Boolean {
        val eventStart: Date? = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(event.date + " " + event.startTime)
        val eventEnd: Date? = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(event.date + " " + event.endTime)
        val currentDate = Date()

        if (eventStart != null && eventEnd != null) {
            if (eventStart <= currentDate && currentDate <= eventEnd)
                return true
        }

        return false
    }

    @SuppressLint("SimpleDateFormat")
    fun eventEnded(event: Event): Boolean {
        val eventEnd: Date? = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(event.date + " " + event.endTime)
        val currentDate = Date()

        if (eventEnd != null) {
            if (currentDate > eventEnd)
                return true
        }

        return false
    }
}