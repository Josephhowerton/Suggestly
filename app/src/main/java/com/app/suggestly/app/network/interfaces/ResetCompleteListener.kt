package com.app.suggestly.app.network.interfaces

import com.app.suggestly.app.network.auth.AuthResponse
import com.app.suggestly.ui.auth.reset.ResetEmail

interface ResetCompleteListener {
    fun onSuccess(response: AuthResponse<ResetEmail>)
    fun onFailed(response: AuthResponse<ResetEmail>)
}