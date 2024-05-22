package com.example.kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import pub.devrel.easypermissions.BuildConfig

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ToDo - check how to create files, how to flatten data

        configureFirebaseServices()
        setContent {
            kotlinApp()
        }
    }

    private fun configureFirebaseServices(){
        if (BuildConfig.DEBUG){
            Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
            Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        }
    }
}

