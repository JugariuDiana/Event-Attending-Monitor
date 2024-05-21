package com.example.kotlin.screens.BLE

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin.R
import com.example.kotlin.activities.BLEActivity
import com.example.kotlin.domain.Attendee


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothListScreen(
    eventId: String,
    popUpScreen: () -> Unit,
    restartApp: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BleScannerViewModel = hiltViewModel(),
    deviceListAdaptor: LeDeviceListAdapter = LeDeviceListAdapter()
) {
    //    LaunchedEffect(Unit) {
//        context.startActivity(Intent(context, BLEActivity::class.java).apply {
//            putExtra("eventId", eventId)
//        })
//    }

    val intent = Intent(LocalContext.current, BLEActivity::class.java)
    intent.putExtra("eventId", eventId)
    startActivity(LocalContext.current, intent, null)
    val attendees by deviceListAdaptor.deviceList.collectAsStateWithLifecycle(emptyList())
    LaunchedEffect(Unit) { viewModel.initialize(eventId, restartApp) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
                IconButton(onClick = popUpScreen) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
//        Log.d("bleScan", "devices ${devices.size}")
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 15.dp,
                    bottom = 15.dp
                )
            ) {
                items(attendees) { attendee ->
                    AttendanceItem(
                        viewModel = viewModel,
                        attendee = attendee
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceItem(
    viewModel: BleScannerViewModel,
    attendee: Attendee
){
    val attendeeName by produceState(initialValue = "", viewModel, attendee) {
        value = viewModel.getAttendeeName(attendee.userId)
    }


    Card(
        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = attendeeName,
                modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(text = attendee.firstTimeSeen + "->" + attendee.lastTimeSeen,
                style = MaterialTheme.typography.bodyLarge )
        }
    }
}