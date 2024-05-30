package com.example.kotlin.screens.BLE

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kotlin.domain.Attendee
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.getAndUpdate

class  LeDeviceListAdapter:ViewModel() {
    private val list = emptyList<Attendee>()
    private val _deviceList: MutableStateFlow<List<Attendee>> = MutableStateFlow(list)
    val deviceList: Flow<List<Attendee>> get() = flow {
        while (true){
        val deviceList = _deviceList.value
        delay(100)
        emit(deviceList)}
    }

//    fun addDevice(attendee: Attendee) {
//        Log.d("bleScan", "adding device + ${attendee.id}")
//        val currentList = _deviceList.value
//        if (currentList.none { it.id == attendee.id }) {
//            _deviceList.value = currentList + attendee
//        } else {
//            for (device in _deviceList.value){
//                if (device.id == attendee.id){
//                    device.lastTimeSeen = attendee.id
//                }
//            }
//        }
//    }

    fun addDevice(attendee: Attendee) {
        val currentList = _deviceList.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == attendee.id }

        if (existingIndex != -1) {
            currentList[existingIndex] = attendee
        } else {
            currentList.add(attendee)
        }

        _deviceList.value = currentList
    }

    fun emptyList(){
        _deviceList.value = kotlin.collections.emptyList()
    }
}


