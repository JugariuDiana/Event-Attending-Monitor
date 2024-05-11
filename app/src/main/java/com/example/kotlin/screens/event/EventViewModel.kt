package com.example.kotlin.screens.event

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import com.example.kotlin.account.EventStorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.screens.AppViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val accountService: AccountService,
    private val eventStorageService: EventStorageService
) : AppViewModel() {
    lateinit var event: MutableStateFlow<Event>

    fun initialize(eventId: String, restartApp: (String) -> Unit) {
        launchCatching {
            event.value = eventStorageService.readEvent(eventId)!!
        }
//        try {
//
////            myRef.setValue("with security")
////            myRef.removeValue()
//            myRef.child("users").child("1").setValue("name")
//            myRef.child("users").child("2").setValue("name")
//        } catch (e: Throwable) {
//            Log.e("add", "Error occurred: $e")
//        }
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
}