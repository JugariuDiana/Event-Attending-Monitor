//package com.example.kotlin
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.example.kotlin.databinding.ActivityMainBinding
//import com.example.kotlin.screens.BluetoothListScreen
//import com.example.kotlin.ui.theme.KotlinTheme
//import pub.devrel.easypermissions.EasyPermissions
//
//
//class EventActivity2 : ComponentActivity(), EasyPermissions.PermissionCallbacks {
//    lateinit var  binding: ActivityMainBinding
//    companion object{
//        const val PERMISSION_REQUEST_CODE = 1
//    }
//
//    var showPermissionDialog by mutableStateOf(false)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val requestPermissions = RequestPermissions(context = this@EventActivity2, activity = this)
//
//        setContent {
//            KotlinTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    hasPermission()
//                    if (showPermissionDialog) {
//                        PermissionRequestDialog(onDismissRequest = { showPermissionDialog = false }, this@EventActivity2)
//                    } else {
//                        val bleScanner = BleScanner(context = this@EventActivity2, requestPermissions)
//                        Surface(color = MaterialTheme.colorScheme.background) {
//                            show(bleScanner.getLeDeviceListAdapter())
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        showPermissionDialog = false
//        val bleScanner = BleScanner(context = this@EventActivity2, activity = this)
//        bleScanner.scan()
//    }
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        showPermissionDialog = true
//    }
//
//    private fun hasPermission(){
//        if (EasyPermissions.hasPermissions(
//            this,
//            android.Manifest.permission.BLUETOOTH_SCAN,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//        )) {}
//        else {
//            requestPermissionsForScan()
//        }
//    }
////    TO DO call at permissions check before actual scanning
//
//
//     fun requestPermissionsForScan(){
//        EasyPermissions.requestPermissions(
//            this,
//            "Bluetooth and Location permissions",
//            PERMISSION_REQUEST_CODE,
//            android.Manifest.permission.BLUETOOTH_SCAN,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//        )
//    }
//}
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun show(leDeviceListAdapter: LeDeviceListAdapter) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Cyan),
//                title = {
//                    Text(text = "Smt", color = Color.White)
//                }
//            )
//        }
//    ) {
//        Column (
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Spacer(modifier = Modifier.height(60.dp))
//            BluetoothListScreen(deviceListAdaptor = leDeviceListAdapter)
//        }
//    }
//}
//
//@Composable
//fun PermissionRequestDialog(onDismissRequest: () -> Unit, activity: EventActivity2) {
//    AlertDialog(
//        onDismissRequest = onDismissRequest,
//        title = {
//            Text(text = "Requesting necessary permissions")
//        },
//        text = {
//            Text(text = "Bluetooth and Location permissions are needed")
//        },
//        confirmButton = {
//            Button(onClick = {
//                activity.requestPermissionsForScan()
//                onDismissRequest()
//            }){
//                Text(text = "Requesting permissions")
//            }
//        },
//        dismissButton = {
//            IconButton(onClick = onDismissRequest) {
//                Icon(Icons.Default.Close, contentDescription = "Close")
//            }
//        }
//    )
//}