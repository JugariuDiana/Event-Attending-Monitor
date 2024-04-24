package com.example.kotlin

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.kotlin.domain.BleId
import pub.devrel.easypermissions.EasyPermissions


class BleScanner (private val context: Context, private val activity: EventActivity) {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner : BluetoothLeScanner
    private var leDeviceListAdapter = LeDeviceListAdapter()
    private var scanning = false
    private val handler = Handler()
    private val SCAN_PERIOD: Long = 20000 //20 seconds

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun scan(){
        scanLeDevice()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun scanLeDevice() {
        Log.d("bleScan", "scanLeDevice: $scanning")
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
                        PERMISSION_REQUEST_CODE,
                        Manifest.permission.BLUETOOTH_SCAN,
                    )
//                    Log.d("bleScan", "BLUETOOTH_SCAN")
//                    requestPermissions.requestBluetoothScanPermission()
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
                        PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
//                    Log.d("bleScan", "ACCESS_FINE_LOCATION")
//                    requestPermissions.requestBluetoothAccessFineLocationPermission()
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
                        PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
//                    Log.d("bleScan", "ACCESS_COARSE_LOCATION")
//                    requestPermissions.requestBluetoothAccessCoarseLocationPermission()
                    return@postDelayed
                }
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
//            Log.d("bleScan", "start scan")
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    fun getLeDeviceListAdapter() : LeDeviceListAdapter {
        return leDeviceListAdapter
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("bleScan", "callback ${result.device}")

            val deviceName = result.scanRecord?.deviceName

            val scanRecord = result.scanRecord
            var data = java.lang.String("")
            if (scanRecord != null) {
                Log.d("bleScan", scanRecord.bytes.toString())
            }
            if (scanRecord != null && scanRecord.serviceUuids != null && scanRecord.serviceUuids.size > 0) {
                data = java.lang.String(scanRecord.serviceUuids[0].toString())
            }
            if (result.scanRecord?.deviceName != null)
                Log.d("bleScan", "callback ${result.device.toString()}")

             val newDevice = BleId(result.device.address.toString(), deviceName, data)
            leDeviceListAdapter.addDevice(newDevice)
        }
    }
}