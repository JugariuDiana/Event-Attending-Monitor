package com.example.kotlin.screens.BLE

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin.LeDeviceListAdapter


@Composable
fun BluetoothListScreen(
    deviceListAdaptor: LeDeviceListAdapter
) {
    Surface {
        val devices by deviceListAdaptor.deviceList.collectAsStateWithLifecycle(emptyList())
//        Log.d("bleScan", "devices ${devices.size}")
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 15.dp,
                bottom = 15.dp
            )
        ) {
            Log.d("bleScan", "ble screen devices ${devices.size}")
            items(devices) { device ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = device.id,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    Modifier.background(Color.Blue),
                    supportingContent = {
                        Text(text = device.deviceName + " " + device.advertiseData)
                    }
                )
            }
        }
    }
}
