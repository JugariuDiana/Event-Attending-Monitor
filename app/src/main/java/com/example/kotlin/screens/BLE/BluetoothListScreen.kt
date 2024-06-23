package com.example.kotlin.screens.BLE

import android.annotation.SuppressLint
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin.domain.Attendee


@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothListScreen(
    viewModel: BleScannerViewModel = hiltViewModel(),
) {
    val attendees by viewModel.deviceList.collectAsStateWithLifecycle(emptyList())

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        TopAppBar(
            title = { Text("${viewModel.event.value.name} -> ${viewModel.event.value.location} : Participants") },
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
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
) {
    var attendeeName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(attendee.userId) {
        attendeeName = viewModel.getAttendeeName(attendee.userId)
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
                text = attendeeName + ": " + attendee.firstTimeSeen + "->" + attendee.lastTimeSeen,
                modifier = Modifier
                    .padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
