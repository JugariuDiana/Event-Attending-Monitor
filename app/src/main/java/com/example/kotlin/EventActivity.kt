package com.example.kotlin

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.kotlin.databinding.ActivityMainBinding
import com.example.kotlin.screens.BluetoothListScreen
import com.example.kotlin.ui.theme.KotlinTheme
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.UUID


class EventActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks{
    lateinit var  binding: ActivityMainBinding

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 123
    }

    var permissionGranted by mutableStateOf(false)
    lateinit var bleScanner: BleScanner

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions()){
            requestPermissionsForScan()
        } else {
            onPermissionsGranted()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @AfterPermissionGranted(PERMISSION_REQUEST_BLUETOOTH_CODE)
    private fun onPermissionsGranted() {
        permissionGranted = true
        startBluetooth()
        startLocation()
        if (isEnabledBluetooth() == true && isEnabledLocation() == true){
            bleScanner = BleScanner(this@EventActivity, this)
            advertise()
            bleScanner.scan()

            setContent{
                KotlinTheme {
                    show(leDeviceListAdapter = bleScanner.getLeDeviceListAdapter())
                }
            }
        }
    }

    private fun isEnabledBluetooth(): Boolean? {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        return bluetoothAdapter?.isEnabled
    }

    private fun isEnabledLocation(): Boolean? {
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
        val parcelUuid = ParcelUuid(UUID.fromString("1b43d840-f655-44c0-b25b-ba00b0a77ce5"))
        //TODO here set parcel UUID to be the UUID id of the user

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
                        this@EventActivity,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    EasyPermissions.requestPermissions(
                        this@EventActivity,
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

        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback)
    }

    fun startBluetooth() {
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

    fun startLocation() {
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun show(leDeviceListAdapter: LeDeviceListAdapter) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Blue),
                title = {
                    Text(text = "Participants", color = Color.White)
                }
            )
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            BluetoothListScreen(deviceListAdaptor = leDeviceListAdapter)
        }
    }
}