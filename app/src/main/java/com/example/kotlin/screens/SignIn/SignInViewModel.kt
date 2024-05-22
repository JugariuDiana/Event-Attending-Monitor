package com.example.kotlin.screens.SignIn

import com.example.kotlin.AppViewModel
import com.example.kotlin.EVENT_LIST_SCREEN
import com.example.kotlin.SIGN_IN_SCREEN
import com.example.kotlin.SIGN_UP_SCREEN
import com.example.kotlin.storage.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
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

    fun cleanData(){
        email.value = email.value.trim()
        password.value = password.value.trim()
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit){
        launchCatching {
            cleanData()
            accountService.signIn(email.value, password.value)
            openAndPopUp(EVENT_LIST_SCREEN, SIGN_IN_SCREEN)
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit){
        openAndPopUp(SIGN_UP_SCREEN, SIGN_IN_SCREEN)
    }
}