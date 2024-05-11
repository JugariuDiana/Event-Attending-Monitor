package com.example.kotlin.screens.eventList

import android.util.Log
import com.example.kotlin.ADD_EVENT_SCREEN
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.EVENT_ID
import com.example.kotlin.EVENT_SCREEN
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.EventStorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.screens.AppViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
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

    fun onNoteClick(openScreen: (String) -> Unit, note: Event) {
        openScreen("$EVENT_SCREEN?$EVENT_ID=${note.id}")
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