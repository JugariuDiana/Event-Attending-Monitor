package com.example.kotlin.screens.event

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import com.example.kotlin.ui.theme.KotlinTheme
import java.text.SimpleDateFormat
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddEventScreen(
    popUpScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEventViewModel = hiltViewModel()
) {
    val name = viewModel.name.collectAsState()
    val location = viewModel.location.collectAsState()
    val availableSeats = viewModel.availableSeats.collectAsState()
    val date = viewModel.date.collectAsState()
    val startTimeState = rememberTimePickerState(12, 30, true)
    val endTimeState = rememberTimePickerState(startTimeState.hour + 1, 30, true)

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var nameValidation by remember { mutableStateOf(true) }
    var locationValidation by remember { mutableStateOf(true) }
    var availableSeatsValidation by remember { mutableStateOf(true) }
    var timeValidation by remember { mutableStateOf(true) }

    fun validateAllFields(){
        nameValidation = name.value.isNotEmpty()
        locationValidation = location.value.isNotEmpty()
        availableSeatsValidation = availableSeats.value != 0
        timeValidation = isEndTimeValid(startTimeState, endTimeState)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        TopAppBar(
            title = { Text("New event") },
            actions = {
                IconButton(onClick = {
                    validateAllFields()
                    if (timeValidation && nameValidation && locationValidation &&
                        availableSeatsValidation){
                        viewModel.addEvent(popUpScreen) }
                }) {
                    Icon(Icons.Filled.Done, "Save event")
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
                value = name.value,
                onValueChange = {
                    viewModel.updateName(it)
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
                value = location.value,
                onValueChange = {
                    viewModel.updateLocation(it)
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
                value = availableSeats.value.toString(),
                onValueChange = {
                    if (it.toIntOrNull() != null){
                        viewModel.updateAvailableSeats(it.toInt())
                        availableSeatsValidation = it.toInt() > 0
                    } else {
                        viewModel.updateAvailableSeats(0)
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
                Text(text = "Available seats must be a number (bigger than 0)", color = Color.Red)
            }

            Button(onClick = { showDatePicker = true }) {
                Text(text = "Date: " + date.value)
            }

            if (showDatePicker) {
                MyDatePickerDialog(
                    onDateSelected = { viewModel.updateDate(it) },
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
                                viewModel.updateStartTime("${startTimeState.hour}:${startTimeState.minute}")
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
                                viewModel.updateEndTime("${endTimeState.hour}:${endTimeState.minute}")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis > System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewEventPreview() {
    KotlinTheme {
        AddEventScreen({})
    }
}

//ToDo -numbers can not be null, if empty box make number be null, check fields value before submitting the event creation