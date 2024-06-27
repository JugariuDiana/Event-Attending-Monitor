package com.example.kotlin.screens.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlin.domain.Attendee
import com.example.kotlin.ui.theme.KotlinTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EndEventScreen(
    eventId: String,
    popUpScreen: () -> Unit,
    restartApp: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EndEventViewModel = hiltViewModel()
) {
    val event = viewModel.event.collectAsState()
    val attendees = viewModel.attendees.collectAsState()
    val attendeesDetected = viewModel.attendeesDetected.collectAsState()

    LaunchedEffect(Unit) { viewModel.initialize(eventId, restartApp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(
            title = {
                Text(text = event.value.name)
            },
            navigationIcon = {
                IconButton(onClick = popUpScreen) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp)
        ) {
            item {
                Text(text = "Event information: ",
                    color = Color.Red)
            }

            item {
                TextField(
                    value = event.value.location,
                    onValueChange = {},
                    label = { Text("Event location") },
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                TextField(
                    value = event.value.availableSeats.toString(),
                    onValueChange = {},
                    label = { Text("Number of available seats") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                TextField(
                    value = event.value.reservedSeats.toString(),
                    label = { Text("Reserved seats") },
                    onValueChange = {},
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                TextField(
                    value = event.value.date,
                    label = { Text("Date") },
                    onValueChange = {},
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                TextField(
                    value = event.value.startTime,
                    label = { Text("Start time") },
                    onValueChange = {},
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                TextField(
                    value = event.value.endTime,
                    label = { Text("End time") },
                    onValueChange = {},
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            item {
                Text(text = "Detected participants: ",
                    color = Color.Red)
            }

            items(attendeesDetected.value) { attendee ->
                AttendanceItem(
                    viewModel = viewModel,
                    attendee = attendee
                )
            }

            item {
                Text(text = "All registered participants: ",
                    color = Color.Red)
            }

            items(attendees.value) { attendee ->
                AttendanceItem(
                    viewModel = viewModel,
                    attendee = attendee
                )
            }
        }
    }
}

@Composable
fun AttendanceItem(
    viewModel: EndEventViewModel,
    attendee: Attendee
) {
    var attendeeName by remember { mutableStateOf("Loading...") }
    var time = "did not attend"

    LaunchedEffect(attendee.userId) {
        attendeeName = viewModel.getAttendeeName(attendee.userId)
    }

    if (attendee.firstTimeSeen.isNotEmpty()) {
        time = "${attendee.firstTimeSeen} -> ${attendee.lastTimeSeen}"
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
                text = "$attendeeName: $time",
                modifier = Modifier
                    .padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EndEventPreview() {
    KotlinTheme {
        EndEventScreen(eventId = "-1", {}, {})
    }
}
