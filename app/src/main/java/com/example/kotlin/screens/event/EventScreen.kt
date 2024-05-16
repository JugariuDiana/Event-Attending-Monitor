package com.example.kotlin.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
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
import com.example.kotlin.MyDatePickerDialog
import com.example.kotlin.ui.theme.KotlinTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EventScreen(
    eventId: String,
    popUpScreen: () -> Unit,
    restartApp: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventViewModel = hiltViewModel()
) {
    val event = viewModel.event.collectAsState()
    val startTimeState = rememberTimePickerState(12, 30, true)
    val endTimeState = rememberTimePickerState(startTimeState.hour + 1, 30, true)

    LaunchedEffect(Unit) { viewModel.initialize(eventId, restartApp) }

    var nameValidation by remember { mutableStateOf(true) }
    var locationValidation by remember { mutableStateOf(true) }
    var availableSeatsValidation by remember { mutableStateOf(true) }
    var timeValidation by remember { mutableStateOf(true) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    fun validateAllFields(){
        nameValidation = event.value.name.isNotEmpty()
        locationValidation = event.value.location.isNotEmpty()
        availableSeatsValidation = event.value.availableSeats != 0 &&
                event.value.availableSeats >= event.value.reservedSeats
        timeValidation = isEndTimeValid(startTimeState, endTimeState)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        TopAppBar(
            title = {
                IconButton(onClick = popUpScreen ){
                    Icon(Icons.Filled.ArrowBack, "Back")}},
            actions = {
                IconButton(onClick = {
                    validateAllFields()
                    if (nameValidation && locationValidation && availableSeatsValidation && timeValidation)
                        viewModel.saveEvent(popUpScreen) }) {
                    Icon(Icons.Filled.Done, "Save event")
                }
                IconButton(onClick = { viewModel.deleteEvent(popUpScreen) }) {
                    Icon(Icons.Filled.Delete, "Save event")
                }
            }
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = event.value.name,
                onValueChange = {
                    event.value.name = it
                    viewModel.updateEvent(event.value)
                    nameValidation = it.isNotBlank() },
                label = { Text("Event name")},
                isError = !nameValidation,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

            if (!nameValidation){
                Text(text = "Name can not be empty", color = Color.Red)
            }

            TextField(
                value = event.value.location,
                onValueChange = {
                    event.value.location = it
                    viewModel.updateEvent(event.value)
                    locationValidation = it.isNotBlank()},
                label = { Text("Event location")},
                isError = !locationValidation,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            if (!locationValidation){
                Text(text = "Location can not be empty", color = Color.Red)
            }

            TextField(
                value = event.value.availableSeats.toString(),
                onValueChange = {
                    if (it.toIntOrNull() != null){
                        event.value.availableSeats = it.toInt()
                        viewModel.updateEvent(event.value)
                        availableSeatsValidation = it.toInt() > 0 && it.toInt() >= event.value.reservedSeats
                    } else {
                        event.value.availableSeats = event.value.reservedSeats
                        viewModel.updateEvent(event.value)
                        availableSeatsValidation = false
                    }
                },
                label = { Text("Number of available seats (only numbers)")},
                isError = !availableSeatsValidation,
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

            if (!availableSeatsValidation){
                Text(text = "Available seats must be a number (bigger than reserved seats)", color = Color.Red)
            }

            TextField(
                value = event.value.reservedSeats.toString(),
                label = { Text("Reserved seats")},
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

            Button(onClick = { showDatePicker = true }) {
                Text(text = "Date: " + event.value.date)
            }

            if (showDatePicker) {
                MyDatePickerDialog(
                    onDateSelected = { event.value.date = it
                        viewModel.updateEvent(event.value) },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showStartTimePicker){
                AlertDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(color = Color.Gray)
                            .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = startTimeState)
                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showStartTimePicker = false }) {
                                Text(text = "Dismiss")
                            }
                            TextButton(onClick = {
                                showStartTimePicker = false
                                timeValidation = isEndTimeValid(startTimeState, endTimeState)
                                event.value.startTime = "${startTimeState.hour}:${startTimeState.minute}"
                                viewModel.updateEvent(event.value)
                            }) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { showStartTimePicker = true }) {
                    Text(text = "Start Time: " + startTimeState.hour + ":" + startTimeState.minute)
                }
            }

            if (showEndTimePicker){
                AlertDialog(
                    onDismissRequest = { showEndTimePicker = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(color = Color.Gray)
                            .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = endTimeState)
                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showEndTimePicker = false }) {
                                Text(text = "Dismiss")
                            }
                            TextButton(onClick = {
                                showEndTimePicker = false
                                timeValidation = isEndTimeValid(startTimeState, endTimeState)
                                event.value.endTime = "${endTimeState.hour}:${endTimeState.minute}"
                                viewModel.updateEvent(event.value)
                            }) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { showEndTimePicker = true }) {
                    Text(text = "End Time: " + endTimeState.hour + ":" + endTimeState.minute)
                }
            }

            if (!timeValidation){
                Text(text = "End time must be latter than start time", color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun isEndTimeValid(startTimeState: TimePickerState, endTimeState: TimePickerState): Boolean {
    return endTimeState.hour > startTimeState.hour ||
            (endTimeState.hour == startTimeState.hour && endTimeState.minute > startTimeState.minute)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventPreview() {
    KotlinTheme {
        EventScreen(eventId = "-1", {}, {})
    }
}
