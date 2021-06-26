package com.josephhowerton.suggestly.ui.auth.register

import com.josephhowerton.suggestly.ui.auth.signin.LoggedInUserView

data class RegisterResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null,
        val message: String? = null
)