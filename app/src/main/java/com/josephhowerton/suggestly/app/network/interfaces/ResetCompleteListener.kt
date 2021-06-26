package com.josephhowerton.suggestly.app.network.interfaces

import com.josephhowerton.suggestly.app.network.auth.AuthResult
import com.josephhowerton.suggestly.ui.auth.reset.ResetEmail

interface ResetCompleteListener {
    fun onSuccess(result: AuthResult<ResetEmail>)
    fun onFailed(result: AuthResult<ResetEmail>)
}