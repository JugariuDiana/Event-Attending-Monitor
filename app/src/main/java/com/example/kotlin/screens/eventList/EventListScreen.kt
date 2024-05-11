package com.example.kotlin.screens.eventList

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlin.R
import com.example.kotlin.activities.ui.theme.KotlinTheme
import com.example.kotlin.activities.ui.theme.Purple40
import com.example.kotlin.activities.ui.theme.PurpleGrey40
import com.example.kotlin.domain.Event
import com.example.kotlin.domain.getName

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun EventListScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.initialize(restartApp) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick(openScreen) },
                modifier = modifier.padding(16.dp),
                containerColor = Purple40,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) {
        val events by viewModel.events.collectAsState(emptyList())
        var showExitAppDialog by remember { mutableStateOf(false) }
        var showRemoveAccDialog by remember { mutableStateOf(false) }

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showExitAppDialog = true }) {
                        Icon(Icons.Filled.ExitToApp, "Exit app")
                    }
                    IconButton(onClick = { showRemoveAccDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_person_24),
                            contentDescription = "Remove account",
                            tint = PurpleGrey40
                        )
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
                LazyColumn {
                    items(events, key = { it.id }) { eventItem ->
                        EventItem(
                            event = eventItem,
                            onActionClick = { viewModel.onEventClick(openScreen, eventItem) }
                        )
                    }
                }
            }

            if (showExitAppDialog) {
                AlertDialog(
                    title = { Text(stringResource(R.string.sign_out_title)) },
                    text = { Text(stringResource(R.string.sign_out_description)) },
                    dismissButton = {
                        Button(onClick = { showExitAppDialog = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.onSignOutClick()
                            showExitAppDialog = false
                        }) {
                            Text(text = stringResource(R.string.sign_out))
                        }
                    },
                    onDismissRequest = { showExitAppDialog = false }
                )
            }

            if (showRemoveAccDialog) {
                AlertDialog(
                    title = { Text(stringResource(R.string.delete_account_title)) },
                    text = { Text(stringResource(R.string.delete_account_description)) },
                    dismissButton = {
                        Button(onClick = { showRemoveAccDialog = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.onDeleteAccountClick()
                            showRemoveAccDialog = false
                        }) {
                            Text(text = stringResource(R.string.delete_account))
                        }
                    },
                    onDismissRequest = { showRemoveAccDialog = false }
                )
            }
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    onActionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionClick(event.id) }
        ) {
            Text(
                text = event.getName() + " " + event.location,
                modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventsListPreview() {
    KotlinTheme {
        EventListScreen({ }, { })
    }
}