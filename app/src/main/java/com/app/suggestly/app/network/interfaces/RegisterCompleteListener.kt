package com.app.suggestly.app.network.interfaces

import com.app.suggestly.app.network.auth.AuthResponse
import com.app.suggestly.app.network.auth.LoggedInUser
import com.google.firebase.auth.FirebaseUser

interface RegisterCompleteListener {
    fun onFirebaseSuccess(response: AuthResponse<FirebaseUser>)
    fun onSuccess(response: AuthResponse<Boolean>)
    fun onFailed(response: AuthResponse<LoggedInUser>)
}