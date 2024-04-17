package com.example.kotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

class ManageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            startBLEActivity()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun startBLEActivity() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smt act",
                        color = Color.White
                    )
                },
                Modifier.background(Color(0xff0f9d58))
            )
        }
    ) {
        ShowBleDevices()
    }
}

@Composable
fun ShowBleDevices() {
    val mContext = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                mContext.startActivity(Intent(mContext, EventActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(Color(0XFF0F9D58)),
        ) {
            Text("Go scanning", color = Color.White)
        }
    }
}