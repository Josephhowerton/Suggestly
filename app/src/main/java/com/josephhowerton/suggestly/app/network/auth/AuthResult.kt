package com.josephhowerton.suggestly.app.network.auth

sealed class AuthResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}