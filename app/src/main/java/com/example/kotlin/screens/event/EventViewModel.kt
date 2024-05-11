package com.example.kotlin.screens.event

import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.EventStorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val eventStorageService: EventStorageService
) : AppViewModel() {
    val event = MutableStateFlow(DEFAULT_EVENT)
    var userInformation = User()

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = eventStorageService.readEvent(eventId)!!
        }
        observeAuthenticationState(restartApp)
    }



    private fun observeAuthenticationState(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
                else userInformation = user
            }
        }
    }

    fun updateEvent(new: Event) {
        event.value = new
    }

    fun saveEvent(popUpScreen: () -> Unit) {
        launchCatching {
            eventStorageService.updateEvent(event.value)
        }

        popUpScreen()
    }

    fun deleteEvent(popUpScreen: () -> Unit) {
        launchCatching {
            eventStorageService.deleteEvent(event.value.id)
        }

        popUpScreen()
    }

    companion object {
        private val DEFAULT_EVENT = Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID)
    }
}