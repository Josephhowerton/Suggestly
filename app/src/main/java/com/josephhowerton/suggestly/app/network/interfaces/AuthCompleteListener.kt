package com.josephhowerton.suggestly.app.network.interfaces

import com.josephhowerton.suggestly.app.network.auth.AuthResult
import com.josephhowerton.suggestly.app.network.auth.LoggedInUser

interface AuthCompleteListener {
    fun onSuccess(result: AuthResult<LoggedInUser>)
    fun onFailed(result: AuthResult<LoggedInUser>)
}