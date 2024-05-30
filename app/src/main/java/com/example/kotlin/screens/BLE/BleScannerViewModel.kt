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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    private val SCAN_PERIOD: Long = 20000 //20 seconds
    val event = MutableStateFlow(Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID))
    private var userInformation = MutableStateFlow(User())
    private val users = storageService.users
    lateinit var attendees: List<Attendee>


    private val _deviceList: MutableStateFlow<List<Attendee>> = MutableStateFlow(emptyList<Attendee>())
    val deviceList: Flow<List<Attendee>>
        get() = flow {
        while (true){
            val deviceList = _deviceList.value
            delay(100)
            emit(deviceList)}
    }

    suspend fun getAttendeeName(userId: String): String {
        val usersList = users.first()
        return usersList.find { it.id == userId }?.name!!
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
        while (true) {
            if (accountService.currentUserId == event.value.organizerId) {
                scanLeDevice(context, activity)
                delay(20000) // Must change, maybe scan for 1 minute every five minutes
                Log.d("dataMonitoring", "start scanning again")
            }
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
                    for (attendance in attendees) {
                        if (attendance.id == data) {
                            if (attendance.firstTimeSeen == "") {
                                attendance.firstTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                                attendance.lastTimeSeen = attendance.firstTimeSeen
                            } else {
                                attendance.lastTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                            }

                            if (!currentUserAttendance.attendees.contains(data)) {
                                attendance.attendees = attendance.attendees + data
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
                    for (attendance in attendees) {
                        if (attendance.id == data) {
                            if (attendance.firstTimeSeen == "") {
                                attendance.firstTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                                attendance.lastTimeSeen = attendance.firstTimeSeen
                            } else {
                                attendance.lastTimeSeen = LocalTime.now().hour.toString() + ":" + LocalTime.now().minute.toString()
                            }

                            if (!currentUserAttendance.attendees.contains(data)) {
                                attendance.attendees = attendance.attendees + data
                            }

                            launchCatching {
                                storageService.updateAttendee(attendance)
                            }
                            addDevice(attendance)

                            for (previouslyDetectedAttendance in attendance.attendees){
                                addDevice(getAttendance(previouslyDetectedAttendance)!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addDevice(attendee: Attendee) {
        val currentList = _deviceList.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == attendee.id }

        if (existingIndex != -1) {
            currentList[existingIndex] = attendee
        } else {
            currentList.add(attendee)
        }

        _deviceList.swapList(currentList)
    }

    fun <T> MutableStateFlow<T>.swapList(newList: MutableList<Attendee>){
        //Todo - check phone to make sure list update is correct
    }
}
