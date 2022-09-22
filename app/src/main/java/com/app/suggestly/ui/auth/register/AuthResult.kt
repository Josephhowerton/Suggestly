package com.app.suggestly.ui.auth.register

import com.app.suggestly.ui.auth.signin.LoggedInUserView

data class AuthResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null,
        val message: String? = null
)