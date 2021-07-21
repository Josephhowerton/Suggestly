package com.josephhowerton.suggestly.app.network.interfaces

import com.josephhowerton.suggestly.app.network.auth.AuthResponse
import com.josephhowerton.suggestly.ui.auth.reset.ResetEmail

interface ResetCompleteListener {
    fun onSuccess(response: AuthResponse<ResetEmail>)
    fun onFailed(response: AuthResponse<ResetEmail>)
}