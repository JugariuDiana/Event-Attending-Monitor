package com.example.kotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kotlin.screens.BLE.BluetoothListScreen
import com.example.kotlin.ui.theme.KotlinTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.kotlin.screens.BLE.BleScannerViewModel
import com.example.kotlin.LOG_TAG
import com.example.kotlin.LeDeviceListAdapter
import com.example.kotlin.RequestPermissions
import java.util.UUID

class EventActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 123
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermissions = RequestPermissions(context = this@EventActivity, activity = this)

        setContent {
            KotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bleScanner = BleScannerViewModel(context = this@EventActivity, requestPermissions)
                    bleScanner.scan()
                    Surface(color = MaterialTheme.colorScheme.background) {
                        advertise()
                        show(bleScanner.getLeDeviceListAdapter())
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun advertise(){
        val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser

        val parameters = AdvertisingSetParameters.Builder()
            .setLegacyMode(true) // True by default, but set here as a reminder.
            .setConnectable(false)
            .setScannable(true)
            .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
            .build()


        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
//                .addTransportDiscoveryData(TransportDiscoveryData("143252".toByteArray()))
            .addManufacturerData(15, "15823".toByteArray(Charsets.UTF_8))
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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                currentAdvertisingSet.setAdvertisingData(
                    AdvertiseData.Builder().setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build()
                )
                currentAdvertisingSet.setScanResponseData(
                    AdvertiseData.Builder().addServiceUuid(ParcelUuid(UUID.randomUUID())).build()
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

