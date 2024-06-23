package com.example.kotlin.screens.BLE

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat
import com.example.kotlin.AppViewModel
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.PERMISSION_REQUEST_BLUETOOTH_CODE
import com.example.kotlin.activities.BLEActivity
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.storage.AccountService
import com.example.kotlin.storage.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pub.devrel.easypermissions.EasyPermissions
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class BleScannerViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : AppViewModel() {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner : BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()
    private val SCAN_PERIOD: Long = 200000 //20 seconds
    val event = MutableStateFlow(Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID))
    private var userInformation = MutableStateFlow(User())
    lateinit var attendees: List<Attendee>
    lateinit var localContext: Context

    private val _deviceList: MutableStateFlow<List<Attendee>> = MutableStateFlow(emptyList())
    val deviceList: StateFlow<List<Attendee>> get() = _deviceList.asStateFlow()

    fun addDevice(attendee: Attendee) {
        val newAttendance = Attendee(attendee.id, attendee.userId, attendee.firstTimeSeen, attendee.lastTimeSeen, attendee.attendees)
        _deviceList.update {currentList ->
            val updatedList = currentList.toMutableList()
            val existingIndex = updatedList.indexOfFirst { it.id == attendee.id }
            if (existingIndex != -1) {
                updatedList.removeAt(existingIndex)
            }
            updatedList.add(newAttendance)
            updatedList
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopBleScanning()
    }

    private fun stopBleScanning() {
        if (scanning) {
            if (ActivityCompat.checkSelfPermission(
                    localContext,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            bluetoothLeScanner?.stopScan(leScanCallback)
            bluetoothLeScanner?.stopScan(leScanCallbackOrganizer)
            scanning = false
        }
        handler.removeCallbacksAndMessages(null)
    }

    suspend fun getAttendeeName(userId: String): String {
        val user = storageService.getUser(userId) ?: return "can not load name"
        return user.name
    }

    fun initialize(eventId: String){
        launchCatching {
            event.value = storageService.readEvent(eventId)!!
            userInformation.value = accountService.getUser(accountService.currentUserId)!!
            attendees = storageService.getEventAttendees(event.value.attendeesList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun scan(context: Context, activity: BLEActivity){
        localContext = context
        while (true) {
            scanLeDevice(context, activity)
            delay(20000) // Must change, maybe scan for 1 minute every five minutes
            Log.d("dataMonitoring", "start scanning again")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun scanLeDevice(context: Context, activity: BLEActivity) {
        Log.d("bleScan", "scanLeDevice: $scanning")
        Log.d("dataMonitoring", "eventid ->" + event.value.id)

        val callback = if (accountService.currentUserId == event.value.organizerId){
            leScanCallbackOrganizer
        } else {
            leScanCallback
        }

        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                Log.d("bleScan", "post delayed")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    EasyPermissions.requestPermissions(
                        activity,
                        "Bluetooth and Location permissions",
                        PERMISSION_REQUEST_BLUETOOTH_CODE,
                        Manifest.permission.BLUETOOTH_SCAN,
                    )
                    return@postDelayed
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    EasyPermissions.requestPermissions(
                        activity,
                        "Bluetooth and Location permissions",
                        PERMISSION_REQUEST_BLUETOOTH_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                    return@postDelayed
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    EasyPermissions.requestPermissions(
                        activity,
                        "Bluetooth and Location permissions",
                        PERMISSION_REQUEST_BLUETOOTH_CODE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                    return@postDelayed
                }
                bluetoothLeScanner?.stopScan(callback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner?.startScan(callback)
        } else {
            scanning = false
            bluetoothLeScanner?.stopScan(callback)
            handler.removeCallbacksAndMessages(null)
        }
    }

    fun getAttendance(id : String) : Attendee? {
        return attendees.find { it.id == id }
    }

    fun getCurrentUserAttendance() : Attendee? {
        return attendees.find { it.userId == accountService.currentUserId }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("dataMonitoring", event.value.id)
            val scanRecord = result.scanRecord
            val data: String
            val currentUserAttendance = getCurrentUserAttendance()!!

            if (scanRecord != null && scanRecord.serviceUuids != null && scanRecord.serviceUuids.size > 0) {
                data = scanRecord.serviceUuids[0].toString()

                val attendeesList = event.value.attendeesList

                if (attendeesList.contains(data)){
                    Log.d("shutDown", "$data -> ${LocalTime.now()}")
                    for (attendance in attendees) {
                        if (attendance.id == data) {
                            if (attendance.firstTimeSeen == "") {
                                attendance.firstTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                                attendance.lastTimeSeen = attendance.firstTimeSeen
                            } else {
                                attendance.lastTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                            }

                            if (!currentUserAttendance.attendees.contains(data)) {
                                currentUserAttendance.attendees = currentUserAttendance.attendees + data
                            }

                            for (foundAttendee in attendance.attendees){
                                if (!currentUserAttendance.attendees.contains(foundAttendee)){
                                    currentUserAttendance.attendees += foundAttendee
                                }
                            }

                            launchCatching {
                                storageService.updateAttendee(currentUserAttendance)
                            }

                            launchCatching {
                                storageService.updateAttendee(attendance)
                            }
                            addDevice(attendance)
                        }
                    }
                }
            }
        }
    }

    private val leScanCallbackOrganizer: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("dataMonitoring", event.value.id)
            val scanRecord = result.scanRecord
            val data: String
            val currentUserAttendance = getCurrentUserAttendance()!!

            if (scanRecord != null && scanRecord.serviceUuids != null && scanRecord.serviceUuids.size > 0) {
                data = scanRecord.serviceUuids[0].toString()

                val attendeesList = event.value.attendeesList

                if (attendeesList.contains(data)){
                    for (attendance in attendees) {
                        if (attendance.id == data) {
                            if (attendance.firstTimeSeen == "") {
                                attendance.firstTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                                attendance.lastTimeSeen = attendance.firstTimeSeen
                            } else {
                                attendance.lastTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                            }

                            if (!currentUserAttendance.attendees.contains(data)) {
                                currentUserAttendance.attendees = currentUserAttendance.attendees + data
                            }

                            launchCatching {
                                storageService.updateAttendee(attendance)
                            }

                            launchCatching { addDevice(attendance) }

                            launchCatching {
                            for (previouslyDetectedAttendance in attendance.attendees){
                                    val objectAttendee = getAttendance(previouslyDetectedAttendance)
                                    while (objectAttendee == null){
                                        delay(100)
                                    }
                                    launchCatching {
                                        if (objectAttendee.userId != event.value.organizerId)
                                            addDevice(objectAttendee)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
