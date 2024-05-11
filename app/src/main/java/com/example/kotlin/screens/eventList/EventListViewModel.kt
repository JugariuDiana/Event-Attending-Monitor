package com.example.kotlin.screens.eventList

import com.example.kotlin.ADD_EVENT_SCREEN
import com.example.kotlin.EVENT_SCREEN
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.EventStorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val accountService: AccountService,
    eventStorageService: EventStorageService
) : AppViewModel() {
    val events = eventStorageService.events

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

    fun onEventClick(openScreen: (String) -> Unit, event: Event) {
        openScreen("$EVENT_SCREEN/${event.id}")
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
}