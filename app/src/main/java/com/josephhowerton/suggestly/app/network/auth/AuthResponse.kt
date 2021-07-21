package com.josephhowerton.suggestly.app.network.auth

sealed class AuthResponse<out T : Any> {

    data class Success<out T : Any>(val data: T) : AuthResponse<T>()
    data class Error(val exception: Exception) : AuthResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}