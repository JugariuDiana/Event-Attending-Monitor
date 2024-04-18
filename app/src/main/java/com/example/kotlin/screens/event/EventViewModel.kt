package com.example.kotlin.screens.event

import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.StorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    val event = MutableStateFlow(DEFAULT_EVENT)

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = storageService.readEvent(eventId) ?: DEFAULT_EVENT
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

    fun updateEvent(newName: String) {
        event.value = event.value.copy(name = newName)
    }

    fun saveEvent(popUpScreen: () -> Unit) {
        launchCatching {
            if (event.value.id == EVENT_DEFAULT_ID) {
                storageService.createEvent(event.value)
            } else {
                storageService.updateEvent(event.value)
            }
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
        private val DEFAULT_EVENT = Event(EVENT_DEFAULT_ID, "")
    }
}