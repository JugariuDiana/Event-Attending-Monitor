package com.example.kotlin.activities

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kotlin.EVENT_DEFAULT_ID
import com.example.kotlin.LOG_TAG
import com.example.kotlin.ORGANIZER_DEFAULT_ID
import com.example.kotlin.PERMISSION_REQUEST_BLUETOOTH_CODE
import com.example.kotlin.databinding.ActivityMainBinding
import com.example.kotlin.domain.Attendee
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.User
import com.example.kotlin.screens.BLE.BleScannerViewModel
import com.example.kotlin.screens.BLE.BleScannerViewModelFactory
import com.example.kotlin.screens.BLE.BluetoothListScreen
import com.example.kotlin.storage.Implementation.AccountServiceImpl
import com.example.kotlin.storage.Implementation.StorageServiceImpl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.UUID
import javax.inject.Inject

class BLEActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks{
    lateinit var  binding: ActivityMainBinding
    private lateinit var storageService: StorageServiceImpl
    private lateinit var accountService: AccountServiceImpl
    var event = MutableStateFlow(Event(EVENT_DEFAULT_ID, ORGANIZER_DEFAULT_ID))
    private var attendanceId by mutableStateOf("")
    private var permissionGranted by mutableStateOf(false)
    lateinit var attendees: List<Attendee>
    private var userInformation = MutableStateFlow(User())
    private lateinit var users : Flow<List<User>>

    @Inject
    lateinit var viewModelFactory: BleScannerViewModelFactory
    lateinit var bleScannerViewModel: BleScannerViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountService = AccountServiceImpl()
        storageService = StorageServiceImpl(accountService)
        val eventId = intent.getStringExtra("eventId")

        viewModelFactory = BleScannerViewModelFactory(accountService, storageService)
        bleScannerViewModel = ViewModelProvider(this, viewModelFactory).get(BleScannerViewModel::class.java)
        bleScannerViewModel.initialize(eventId!!)

        users = storageService.users

        lifecycleScope.launch{
            event.value = storageService.readEvent(eventId.toString())!!
            attendanceId = storageService.getUsersAttendance(accountService.currentUserId, event.value.attendeesList)
            userInformation.value = accountService.getUser(accountService.currentUserId)!!
            attendees = storageService.getEventAttendees(event.value.attendeesList)
            while (!hasPermissions()){
                requestPermissionsForScan()
            }

            startLocation()
            startBluetooth()
//            while (!isEnabledLocation()){
//                startLocation()
//            }
//
//            while (isEnabledBluetooth() != true){
//                startBluetooth()
//            }

            onPermissionsGranted()
        }

        setContent {
            BluetoothListScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermissionsForScan(){
        EasyPermissions.requestPermissions(
            this,
            "Bluetooth and Location permissions",
            PERMISSION_REQUEST_BLUETOOTH_CODE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onPermissionsGranted()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @AfterPermissionGranted(PERMISSION_REQUEST_BLUETOOTH_CODE)
    fun onPermissionsGranted() {
        permissionGranted = true
        if (isEnabledBluetooth() == true && isEnabledLocation()){
            GlobalScope.launch {
                while (attendanceId.isEmpty()){
                    delay(100)
                }
                advertise()
                bleScannerViewModel.scan(this@BLEActivity, this@BLEActivity)
            }
        }
    }

    private fun isEnabledBluetooth(): Boolean? {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        return bluetoothAdapter?.isEnabled
    }

    private fun isEnabledLocation(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permissions Required")
            builder.setMessage("Bluetooth and Location permissions are necessary for scanning and advertising devices. Please grant permissions in Settings.")
            builder.setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        } else {
            // User denied permissions but hasn't selected "Never ask again"
            Snackbar.make(
                findViewById(android.R.id.content),
                "Permissions Denied. Please grant permissions to continue.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun advertise(){
        val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
        val parcelUuid = ParcelUuid(UUID.fromString(attendanceId))

        val parameters = AdvertisingSetParameters.Builder()
            .setLegacyMode(true) // True by default, but set here as a reminder.
            .setConnectable(false)
            .setScannable(true)
            .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(parcelUuid)
            .build()

        lateinit var currentAdvertisingSet: AdvertisingSet

        val callback: AdvertisingSetCallback = object : AdvertisingSetCallback() {
            override fun onAdvertisingSetStarted(
                advertisingSet: AdvertisingSet,
                txPower: Int,
                status: Int
            ) {
                Log.i(
                    LOG_TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                            + status
                )
                currentAdvertisingSet = advertisingSet
                if (ActivityCompat.checkSelfPermission(
                        this@BLEActivity,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    EasyPermissions.requestPermissions(
                        this@BLEActivity,
                        "Bluetooth and Location permissions",
                        PERMISSION_REQUEST_BLUETOOTH_CODE,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                    )
                    return
                }

                currentAdvertisingSet.setAdvertisingData(
                    AdvertiseData.Builder().setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build()
                )
                currentAdvertisingSet.setScanResponseData(
                    AdvertiseData.Builder().addServiceUuid(parcelUuid).build()
                )
            }

            override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet, status: Int) {
                Log.i(LOG_TAG, "onAdvertisingDataSet() :status:$status")
            }

            override fun onScanResponseDataSet(advertisingSet: AdvertisingSet, status: Int) {
                Log.i(LOG_TAG, "onScanResponseDataSet(): status:$status")
            }

            override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet) {
                Log.i(LOG_TAG, "onAdvertisingSetStopped():")
            }
        }

        Log.d("bleScan", data.serviceUuids.toString())
        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback)
    }

    private fun startBluetooth() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            startActivityForResult(enableBtIntent, PERMISSION_REQUEST_BLUETOOTH_CODE)
        }
    }

    private fun startLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permissions Required")
            builder.setMessage("Location must be enabled for the following operation.")
            builder.setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }
}