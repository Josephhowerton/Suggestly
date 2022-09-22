package com.app.suggestly.ui.auth.signin

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null,
        val message: String? = null
)