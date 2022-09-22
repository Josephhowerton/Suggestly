package com.app.suggestly.app.network.interfaces

import com.app.suggestly.app.network.auth.AuthResponse
import com.app.suggestly.app.network.auth.LoggedInUser

interface AuthCompleteListener {
    fun onSuccess(response: AuthResponse<LoggedInUser>)
    fun onFailed(response: AuthResponse<LoggedInUser>)
}