package com.app.suggestly.ui.auth.register

data class RegisterFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)