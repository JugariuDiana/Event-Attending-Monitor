package com.example.kotlin

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import com.example.kotlin.domain.BleId


class BleScanner (private val context: Context, private val requestPermissions: RequestPermissions) {
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


    fun scan(){
        scanLeDevice()
    }

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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    Log.d("bleScan", "BLUETOOTH_SCAN")
                    requestPermissions.requestBluetoothScanPermission()
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@postDelayed
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    Log.d("bleScan", "ACCESS_FINE_LOCATION")
                    requestPermissions.requestBluetoothAccessFineLocationPermission()
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@postDelayed
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    Log.d("bleScan", "ACCESS_COARSE_LOCATION")
                    requestPermissions.requestBluetoothAccessCoarseLocationPermission()
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@postDelayed
                }
                Log.d("bleScan", "stop scan")
//                if (EasyPermissions.hasPermissions(
//                        context,
//                        Manifest.permission.BLUETOOTH_SCAN,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                    )) {
//                    bluetoothLeScanner.startScan(leScanCallback)
//                }
//                else {
//                    activity.requestPermissionsForScan()
//                }
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            Log.d("bleScan", "start scan")
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    fun getLeDeviceListAdapter() : LeDeviceListAdapter {
        return leDeviceListAdapter
    }

    fun parseSparseArray(sparseArray: SparseArray<ByteArray>?): ByteArray? {
        if (sparseArray == null) return null

        val totalSize = sparseArray.size()
        var totalByteCount = 0

        // Calculate the total byte count
        for (i in 0 until totalSize) {
            totalByteCount += sparseArray.valueAt(i)?.size ?: 0
        }

        // Create a new ByteArray to hold the concatenated data
        val result = ByteArray(totalByteCount)

        // Copy data from each array into the result array
        var currentIndex = 0
        for (i in 0 until totalSize) {
            val byteArray = sparseArray.valueAt(i)
            if (byteArray != null) {
                System.arraycopy(byteArray, 0, result, currentIndex, byteArray.size)
                currentIndex += byteArray.size
            }
        }

        return result
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("bleScan", "callback ${result.device.toString()}")
            val deviceName = result.scanRecord?.deviceName
            val manufacturerData =
                java.lang.String(parseSparseArray(result.scanRecord?.manufacturerSpecificData))
            if (result.scanRecord?.deviceName != null)
                Log.d("bleScan", "callback ${result.device.toString()}")

//            val newDevice = BleId(result.device.address.toString(), (ByteBuffer.wrap(advertiseData)).toString())
            val newDevice = BleId(result.device.address.toString(), deviceName, manufacturerData)
            leDeviceListAdapter.addDevice(newDevice)
        }
    }
}