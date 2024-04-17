package com.example.kotlin.screens.SignIn

import com.example.kotlin.screens.AppViewModel
import com.example.kotlin.BLUETOOTH_LIST_SCREEN
import com.example.kotlin.SIGN_IN_SCREEN
import com.example.kotlin.SIGN_UP_SCREEN
import com.example.kotlin.account.AccountService
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val accountService: AccountService
): AppViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun updateEmail(newEmail: String){
        email.value = newEmail
    }

    fun updatePassword(newPassword: String){
        password.value = newPassword
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit){
        launchCatching {
            accountService.signIn(email.value, password.value)
            openAndPopUp(BLUETOOTH_LIST_SCREEN, SIGN_IN_SCREEN)
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit){
        openAndPopUp(SIGN_UP_SCREEN, SIGN_IN_SCREEN)
    }
}