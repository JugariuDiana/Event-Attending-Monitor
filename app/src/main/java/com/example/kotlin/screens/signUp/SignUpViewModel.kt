package com.example.kotlin.screens.signUp

import com.example.kotlin.screens.AppViewModel
import com.example.kotlin.EVENT_LIST_SCREEN
import com.example.kotlin.SIGN_UP_SCREEN
import com.example.kotlin.account.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {
    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    fun updateName(newName: String){
        name.value = newName
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun cleanData(){
        email.value = email.value.trim()
        name.value = name.value.trim()
        password.value = password.value.trim()
        confirmPassword.value = confirmPassword.value.trim()
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        cleanData()
        launchCatching {
            if (password.value != confirmPassword.value) {
                throw Exception("Passwords do not match")
            }
            //ToDo create a new user containing all information and generate a unique uuid for him
            accountService.signUp(email.value, password.value)
            openAndPopUp(EVENT_LIST_SCREEN, SIGN_UP_SCREEN)
        }
    }
}