package com.example.kotlin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin.screens.SignIn.SignInScreen
import com.example.kotlin.screens.signUp.SignUpScreen
import com.example.kotlin.screens.splash.SplashScreen
import com.example.kotlin.ui.theme.KotlinTheme

@Composable
fun kotlinApp() {
    KotlinTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            Scaffold { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    AppGraph(appState)
                }
            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        AppState(navController)
    }

fun NavGraphBuilder.AppGraph(appState: AppState) {
//
//    composable(
//        route = "$NOTE_SCREEN$NOTE_ID_ARG",
//        arguments = listOf(navArgument(NOTE_ID) { defaultValue = NOTE_DEFAULT_ID })
//    ) {
//        NoteScreen(
//            noteId = it.arguments?.getString(NOTE_ID) ?: NOTE_DEFAULT_ID,
//            popUpScreen = { appState.popUp() },
//            restartApp = { route -> appState.clearAndNavigate(route) }
//        )
//    }

    composable(SIGN_IN_SCREEN) {
        SignInScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}