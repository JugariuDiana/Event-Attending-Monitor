package com.example.kotlin.screens.splash

import com.example.kotlin.screens.AppViewModel
import com.example.kotlin.EVENT_LIST_SCREEN
import com.example.kotlin.SIGN_IN_SCREEN
import com.example.kotlin.SPLASH_SCREEN
import com.example.kotlin.account.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(EVENT_LIST_SCREEN, SPLASH_SCREEN)
        else openAndPopUp(SIGN_IN_SCREEN, SPLASH_SCREEN)
    }
}