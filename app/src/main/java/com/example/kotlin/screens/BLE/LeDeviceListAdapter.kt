package com.example.kotlin.screens.BLE

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kotlin.domain.Attendee
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class  LeDeviceListAdapter:ViewModel() {
    private val list = emptyList<Attendee>()
    private val _deviceList: MutableStateFlow<List<Attendee>> = MutableStateFlow(list)
    val deviceList: Flow<List<Attendee>> get() = flow {
        while (true){
        val deviceList = _deviceList.value
        delay(100)
        emit(deviceList)}
    }

    fun addDevice(attendee: Attendee) {
        Log.d("bleScan", "adding device + ${attendee.id}")
        val currentList = _deviceList.value
        if (currentList.none { it.id == attendee.id }) {
            _deviceList.value = currentList + attendee
        }
    }

    fun emptyList(){
        _deviceList.value = kotlin.collections.emptyList()
    }
}


