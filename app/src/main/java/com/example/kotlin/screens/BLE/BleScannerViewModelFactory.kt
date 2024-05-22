package com.example.kotlin.screens.BLE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin.domain.Event
import com.example.kotlin.screens.BLE.BleScannerViewModel
import com.example.kotlin.storage.Implementation.AccountServiceImpl
import com.example.kotlin.storage.Implementation.StorageServiceImpl

class BleScannerViewModelFactory(
    private val accountService: AccountServiceImpl,
    private val storageService: StorageServiceImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BleScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BleScannerViewModel(accountService, storageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}