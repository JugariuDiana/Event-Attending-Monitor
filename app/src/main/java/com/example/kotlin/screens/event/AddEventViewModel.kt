package com.example.kotlin.screens.event

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.storage.StorageService
import com.example.kotlin.domain.Event
import com.example.kotlin.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val storageService: StorageService
) : AppViewModel() {
    var name = MutableStateFlow("")
    var location = MutableStateFlow("")
    var availableSeats = MutableStateFlow(0)
    var startTime = MutableStateFlow("12:30")
    var endTime = MutableStateFlow("13:30")
    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    @RequiresApi(Build.VERSION_CODES.O)
    var date = MutableStateFlow(formatted.toString())

    fun updateName(new: String) {
        name.value = new
    }

    fun updateLocation(new: String) {
        location.value = new
    }

    fun updateAvailableSeats(new: Int) {
        availableSeats.value = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStartTime(new: String) {
        startTime.value = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEndTime(new: String) {
        endTime.value = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(new: String) {
        date.value = new
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent(popUpScreen: () -> Unit) {
        launchCatching {
            storageService.createEvent(Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID, name.value
                , location.value, availableSeats.value, 0, startTime.value, endTime.value,
                date.value))
        }
        popUpScreen()
    }
}