package com.josephhowerton.suggestly.ui.auth.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josephhowerton.suggestly.app.Repository
import com.josephhowerton.suggestly.ui.auth.register.RegisterViewModel
import com.josephhowerton.suggestly.ui.auth.reset.ResetViewModel
import com.josephhowerton.suggestly.ui.auth.signin.LoginViewModel

class AuthViewModelFactory(private val application: Application)  : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                repository = Repository.getInstance(application)
            ) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                    repository = Repository.getInstance(application)
            ) as T
        }

        if (modelClass.isAssignableFrom(ResetViewModel::class.java)) {
            return ResetViewModel(
                    repository = Repository.getInstance(application)
            ) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                    repository = Repository.getInstance(application)
            ) as T
        }


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}