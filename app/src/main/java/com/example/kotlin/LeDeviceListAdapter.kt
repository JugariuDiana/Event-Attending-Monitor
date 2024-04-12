package com.example.kotlin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin.domain.BleId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class LeDeviceListAdapter:ViewModel() {
    val list = emptyList<BleId>()
    private val _deviceList: MutableStateFlow<List<BleId>> = MutableStateFlow(list)
    val deviceList: Flow<List<BleId>> get() = flow {
        while (true){
        val deviceList = _deviceList.value
//        Log.d("bleScan", "list size + ${deviceList.size}")
        delay(100)
        emit(deviceList)}
    }//.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addDevice(device: BleId) {
//        Log.d("bleScan", "adding device + ${device.id}")
        val currentList = _deviceList.value
        if (currentList.none { it.id == device.id }) {
            _deviceList.value = currentList + device
        }

    }
}


