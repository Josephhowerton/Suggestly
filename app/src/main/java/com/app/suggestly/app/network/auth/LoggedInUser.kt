package com.app.suggestly.app.network.auth

data class LoggedInUser(
        val userId: String,
        val displayName: String,
        val isNewUser: Boolean? = null
)