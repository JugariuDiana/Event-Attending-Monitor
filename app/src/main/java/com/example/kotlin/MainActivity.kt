package com.example.kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin.activities.ManageActivity
import com.example.kotlin.screens.SignIn.SignInScreen
import com.example.kotlin.screens.signUp.SignUpScreen
import com.example.kotlin.screens.splash.SplashScreen
import com.example.kotlin.ui.theme.KotlinTheme
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun kotlinApp(){
//    KotlinTheme {
//        Surface(color = MaterialTheme.colorScheme.background) {
//            val appState = rememberAppState()
//
//            Scaffold { innerPaddingModifier ->
//                NavHost(
//                    navController = appState.navController,
//                    startDestination = SPLASH_SCREEN,
//                    modifier = Modifier.padding(innerPaddingModifier)
//                ) {
//                    Graph(appState)
//                }
//            }
//        }
//    }
////    Scaffold(
////        topBar = {
////            TopAppBar(
////                title = {
////                    Text(
////                        text = "Menu",
////                        color = Color.White
////                    )
////                },
////                Modifier.background(Color(0xff0f9d58))
////            )
////        }
////    ) {
////        navigateOptions()
////    }
//}
//
//@Composable
//fun rememberAppState(navController: NavHostController = rememberNavController()) =
//    remember(navController) {
//        AppState(navController)
//    }
//
//fun NavGraphBuilder.Graph(appState: AppState) {
////
////    composable(
////        route = "$NOTE_SCREEN$NOTE_ID_ARG",
////        arguments = listOf(navArgument(NOTE_ID) { defaultValue = NOTE_DEFAULT_ID })
////    ) {
////        NoteScreen(
////            noteId = it.arguments?.getString(NOTE_ID) ?: NOTE_DEFAULT_ID,
////            popUpScreen = { appState.popUp() },
////            restartApp = { route -> appState.clearAndNavigate(route) }
////        )
////    }
//
//    composable(SIGN_IN_SCREEN) {
//        SignInScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
//    }
//
//    composable(SIGN_UP_SCREEN) {
//        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
//    }
//
//    composable(SPLASH_SCREEN) {
//        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
//    }
//}
//
////To do - check paths -> does not get to sign in screen
//// don t forget to start emulator before testing, same for bluetooth and location
//
////@Composable
////fun navigateOptions(){
////    val mContext = LocalContext.current
////    Column(
////        Modifier.fillMaxSize(),
////        horizontalAlignment = Alignment.CenterHorizontally,
////        verticalArrangement = Arrangement.Center
////    ) {
////        Button(
////            onClick = {
////                mContext.startActivity(Intent(mContext, ManageActivity::class.java))
////            },
////            colors = ButtonDefaults.buttonColors(Color(0XFF0F9D58)),
////        ) {
////            Text("scanning", color = Color.White)
////        }
////        Button(
////            onClick = {
////                mContext.startActivity(Intent(mContext, SignInActivity::class.java))
////            },
////            colors = ButtonDefaults.buttonColors(Color(0XFF0F9D58)),
////        ) {
////            Text("Sign in", color = Color.White)
////        }
////        Button(
////            onClick = {
////                mContext.startActivity(Intent(mContext, SignInActivity::class.java))
////            },
////            colors = ButtonDefaults.buttonColors(Color(0XFF0F9D58)),
////        ) {
////            Text("Register", color = Color.White)
////        }
////    }
////}
//
////To DO fix flow update
////To DO switch to easy permissions
//
//
