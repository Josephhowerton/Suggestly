package com.josephhowerton.suggestly.app.network.interfaces

import com.josephhowerton.suggestly.app.network.auth.AuthResponse
import com.josephhowerton.suggestly.app.network.auth.LoggedInUser

interface AuthCompleteListener {
    fun onSuccess(response: AuthResponse<LoggedInUser>)
    fun onFailed(response: AuthResponse<LoggedInUser>)
}