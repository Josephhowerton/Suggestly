package com.josephhowerton.suggestly.ui.auth.reset

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josephhowerton.suggestly.app.Repository

class ResetViewModelFactory(private val application: Application)  : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetViewModel::class.java)) {
            return ResetViewModel(
                repository = Repository.getInstance(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}