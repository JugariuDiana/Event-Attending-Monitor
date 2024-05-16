package com.example.kotlin.screens.eventList

import androidx.compose.runtime.collectAsState
import com.example.kotlin.ADD_EVENT_SCREEN
import com.example.kotlin.EVENT_SCREEN
import com.example.kotlin.REGISTER_EVENT_SCREEN
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.UNREGISTER_EVENT_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.StorageService
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
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

    fun onEventClick(openScreen: (String) -> Unit, event: Event, user: User) {
//        if (event.startTime)

        if (event.organizerId == user.id){
            openScreen("$EVENT_SCREEN/${event.id}")
            return
        }
        launchCatching {
            val attendees = storageService.getEventAttendees(event.attendeesList)
            for (attendance in attendees)
                if (attendance.userId == user.id) {
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

//    fun eventIsRunning(event: Event){
//        val eventStartDate: Date = SimpleDateFormat("dd/mm/yyyy")
//        val currentDate = SimpleDateFormat("dd/mm/yyyy").format(Date())
//    }
}