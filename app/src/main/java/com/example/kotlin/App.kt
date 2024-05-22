package com.example.kotlin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlin.activities.BLEActivity
import com.example.kotlin.screens.BLE.BluetoothListScreen
import com.example.kotlin.screens.SignIn.SignInScreen
import com.example.kotlin.screens.event.AddEventScreen
import com.example.kotlin.screens.event.EventScreen
import com.example.kotlin.screens.event.RegisterEventScreen
import com.example.kotlin.screens.event.UnregisterEventScreen
import com.example.kotlin.screens.eventList.EventListScreen
import com.example.kotlin.screens.signUp.SignUpScreen
import com.example.kotlin.screens.splash.SplashScreen
import com.example.kotlin.ui.theme.KotlinTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.AppGraph(appState: AppState) {

//    composable(
//        route = "$EVENT_SCREEN/{$EVENT_ID}",
//        arguments = listOf(navArgument(EVENT_ID) {type = NavType.StringType})
//    ) {backStackEntry ->
//        val eventId = backStackEntry.arguments?.getString(EVENT_ID)
//        if (eventId != null) {
//            EventScreen(
//                eventId = eventId, //it.arguments?.getString(EVENT_ID) ?: "",
//                popUpScreen = { appState.popUp() },
//                restartApp = { route -> appState.clearAndNavigate(route)
//                    Log.d("route", route) }
//            )
//        }
//    }
    composable(
        route = "$EVENT_SCREEN/{$EVENT_ID}",
        arguments = listOf(navArgument(EVENT_ID) {defaultValue = ""})
    ) {
        EventScreen(
            eventId = it.arguments?.getString(EVENT_ID) ?: "",
            popUpScreen = { appState.popUp() },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable(
        route = "$UNREGISTER_EVENT_SCREEN/{$EVENT_ID}",
        arguments = listOf(navArgument(EVENT_ID) {defaultValue = ""})
    ) {
        UnregisterEventScreen(
            eventId = it.arguments?.getString(EVENT_ID) ?: "",
            popUpScreen = { appState.popUp() },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    activity(
        route = "$BLE_ACTIVITY/{$EVENT_ID}"
    ){
        argument("eventId"){type = NavType.StringType}
        activityClass = BLEActivity::class
    }

//    composable(
//        route = "$BLUETOOTH_LIST_SCREEN/{$EVENT_ID}",
//        arguments = listOf(navArgument(EVENT_ID) {defaultValue = ""})
//    ) {
//        BluetoothListScreen(
//            eventId = it.arguments?.getString(EVENT_ID) ?: "",
//            popUpScreen = { appState.popUp() },
//            restartApp = { route -> appState.clearAndNavigate(route) }
//        )
//    }

    composable(
        route = "$REGISTER_EVENT_SCREEN/{$EVENT_ID}",
        arguments = listOf(navArgument(EVENT_ID){})
    ) {
        RegisterEventScreen(
            eventId = it.arguments?.getString(EVENT_ID) ?: "",
            popUpScreen = { appState.popUp() },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable(EVENT_LIST_SCREEN){
        EventListScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) },
            context = LocalContext.current,
        )
    }

    composable(ADD_EVENT_SCREEN){
        AddEventScreen(
            popUpScreen = { appState.popUp() }
        )
    }

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