package com.example.kotlin.screens.event

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlin.ui.theme.KotlinTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UnregisterEventScreen(
    eventId: String,
    popUpScreen: () -> Unit,
    restartApp: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventViewModel = hiltViewModel()
) {
    val event = viewModel.event.collectAsState()

    LaunchedEffect(Unit) { viewModel.initialize(eventId, restartApp) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        TopAppBar(
            title = {
                IconButton(onClick = popUpScreen ){
                    Icon(Icons.Filled.ArrowBack, "Back")}},
            actions = {
                Button(onClick = {
                    viewModel.unRegister(popUpScreen)
                }) {
                    Text(text = "Unregister")
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
                onValueChange = {},
                label = { Text("Event name")},
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            TextField(
                value = event.value.location,
                onValueChange = {},
                label = { Text("Event location")},
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            TextField(
                value = event.value.availableSeats.toString(),
                onValueChange = {},
                label = { Text("Number of available seats (only numbers)")},
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

            TextField(
                value = event.value.date,
                label = { Text("Date")},
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

            TextField(
                value = event.value.startTime,
                label = { Text("Start time")},
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

            TextField(
                value = event.value.endTime,
                label = { Text("End time")},
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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UnregisterEventPreview() {
    KotlinTheme {
        UnregisterEventScreen(eventId = "-1", {}, {})
    }
}
