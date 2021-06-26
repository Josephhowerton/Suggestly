package com.josephhowerton.suggestly.ui.auth.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josephhowerton.suggestly.app.Repository

class RegisterViewModelFactory(private val application: Application)  : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                repository = Repository.getInstance(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}