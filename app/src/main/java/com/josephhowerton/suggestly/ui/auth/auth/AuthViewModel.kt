package com.josephhowerton.suggestly.ui.auth.auth

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.josephhowerton.suggestly.R
import com.josephhowerton.suggestly.app.Repository
import com.josephhowerton.suggestly.app.network.auth.AuthResponse
import com.josephhowerton.suggestly.app.network.auth.LoggedInUser
import com.josephhowerton.suggestly.app.network.interfaces.AuthCompleteListener
import com.josephhowerton.suggestly.ui.auth.register.AuthResult
import com.josephhowerton.suggestly.ui.auth.signin.LoggedInUserView

class AuthViewModel(private val repository: Repository) : ViewModel() {

    private val _registerResult = MutableLiveData<com.josephhowerton.suggestly.ui.auth.register.AuthResult>()
    val registerResult: LiveData<com.josephhowerton.suggestly.ui.auth.register.AuthResult>
        get() = _registerResult

    private val _signInWithEmail = MutableLiveData<Boolean>()
    val signInWithEmail: LiveData<Boolean>
        get() = _signInWithEmail

    private val _signInWithGoogle = MutableLiveData<Boolean>()
    val signInWithGoogle: LiveData<Boolean>
        get() = _signInWithGoogle

    private val _signUp = MutableLiveData<Boolean>()
    val signUp: LiveData<Boolean>
        get() = _signUp

    private val _animate = MutableLiveData<Boolean>()
    val animate: LiveData<Boolean>
        get() = _animate

    private val _navigate = MutableLiveData<Int?>()
    val navigate: LiveData<Int?>
        get() = _navigate

    private val _isLoading = MutableLiveData<Int>()
    val isLoading: LiveData<Int> get() = _isLoading

    val isFoursquareTableFresh: LiveData<Boolean> = repository.isVenueTableFresh

    var destination: Int?
    var isNewUser:Boolean = false

    var email:String = ""
    var password:String = ""

    init{
        destination = null
        _signUp.value = false
        _navigate.value = null
        _isLoading.value = View.GONE
        _signInWithEmail.value = false
        _signInWithGoogle.value = false
    }

    fun onSignInWithEmailClicked(view: View){
        _signInWithEmail.value = true
    }

    fun signInWithEmail(shouldGo: Boolean){
        if(shouldGo){
            _animate.value = shouldGo
            destination = R.id.action_navigation_auth_to_sign_in
            _signInWithEmail.value = false
        }
    }

    fun onSignInWithGoogleClicked(view: View){
        _signInWithGoogle.value = true
    }


    fun signInWithGoogle(data: Intent?){
        try {
            _isLoading.value = View.VISIBLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            repository.loginWithGoogle(account.idToken!!, object : AuthCompleteListener {
                override fun onSuccess(response: AuthResponse<LoggedInUser>) {
                    _isLoading.value = View.GONE
                    destination = R.id.action_navigation_auth_to_navigation_explanation
                    if (response is AuthResponse.Success) {
                        _registerResult.value = AuthResult(success = LoggedInUserView(displayName = response.data.displayName))
                        isNewUser = response.data.isNewUser!!
                    }
                    _animate.value = true
                }

                override fun onFailed(response: AuthResponse<LoggedInUser>) {
                    _isLoading.value = View.GONE
                    if (response is AuthResponse.Error) {
                        _registerResult.value = com.josephhowerton.suggestly.ui.auth.register.AuthResult(message = response.exception.message)
                    }
                    else {
                        _registerResult.value = com.josephhowerton.suggestly.ui.auth.register.AuthResult(error = R.string.login_failed)
                    }
                }
            })

        } catch (e: ApiException) {
            _isLoading.value = View.GONE
            _registerResult.value = com.josephhowerton.suggestly.ui.auth.register.AuthResult(error = R.string.login_failed)
        }
    }

    fun onSignUpClicked(view: View){
        _signUp.value = true
    }
    private val TAG = "TAG"
    fun signUp(shouldGo: Boolean){
        if(shouldGo){
            Log.println(Log.ASSERT, TAG, "CLick");
            _signUp.value = false
            _animate.value = shouldGo
            destination = R.id.action_navigation_auth_to_navigation_register
        }
    }

    fun navigate(){
        _navigate.value = destination
    }

}