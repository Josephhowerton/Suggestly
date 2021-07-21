package com.josephhowerton.suggestly.ui.auth.signin

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.josephhowerton.suggestly.R
import com.josephhowerton.suggestly.app.Repository
import com.josephhowerton.suggestly.app.network.auth.AuthResponse
import com.josephhowerton.suggestly.app.network.auth.LoggedInUser
import com.josephhowerton.suggestly.app.network.interfaces.AuthCompleteListener

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Int>()
    val isLoading: LiveData<Int> get() = _isLoading

    private val _animate = MutableLiveData<Boolean>()
    val animate: LiveData<Boolean>
        get() = _animate

    var email:String = ""
    var password:String = ""

    var destination:Int? = null

    val isFoursquareTableEmpty: LiveData<Boolean> = repository.isVenueTableFresh

    init{
        destination = null
        _loginForm.value = LoginFormState(isDataValid = false)
        _isLoading.value = View.GONE
    }

    fun onNavigateBack(view: View){
        _animate.value = true
        destination = R.id.navigation_auth
    }

    fun onPasswordReset(view: View){
        _animate.value = true
        destination = R.id.navigation_reset
    }

    fun onSignInClick(view: View){
        _isLoading.value = View.VISIBLE
        loginWithEmail()
    }

    fun loginWithEmail() {
        repository.loginWithEmail(email, password, object : AuthCompleteListener {
            override fun onSuccess(response: AuthResponse<LoggedInUser>) {
                if (response is AuthResponse.Success) {
                    animate
                    _loginResult.value = LoginResult(success = LoggedInUserView(displayName = response.data.displayName))
                }
                _animate.value = true

            }

            override fun onFailed(response: AuthResponse<LoggedInUser>) {
                if(response is AuthResponse.Error){
                    _loginResult.value = LoginResult(message = response.exception.message)
                }
                else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
                _isLoading.value = View.GONE
            }
        })
    }

    fun loginDataChanged() {
        if (!isEmailValid()) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else if (!isPasswordValid()) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(): Boolean {
        return password.length > 5
    }

}