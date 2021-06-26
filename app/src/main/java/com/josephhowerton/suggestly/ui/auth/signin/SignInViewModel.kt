package com.josephhowerton.suggestly.ui.auth.signin

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.josephhowerton.suggestly.R
import com.josephhowerton.suggestly.app.Repository
import com.josephhowerton.suggestly.app.network.auth.AuthResult
import com.josephhowerton.suggestly.app.network.auth.LoggedInUser
import com.josephhowerton.suggestly.app.network.interfaces.AuthCompleteListener

class SignInViewModel(private val repository: Repository) : ViewModel() {

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

    init{
        _loginForm.value = LoginFormState(isDataValid = false)
        _isLoading.value = View.GONE
    }

    fun onNavigateBack(view: View){
        _animate.value = true
        destination = R.id.sign_in_to_greeting
    }

    fun onPasswordReset(view: View){
        _animate.value = true
        destination = R.id.action_signInFragment_to_resetFragment
    }

    fun onSignInClick(view: View){
        _isLoading.value = View.VISIBLE
        loginWithEmail()
    }

    fun loginWithEmail() {
        // can be launched in a separate asynchronous job
        repository.loginWithEmail(email, password, object : AuthCompleteListener {
            override fun onSuccess(result: AuthResult<LoggedInUser>) {
                if (result is AuthResult.Success) {
                    animate
                    _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                }
                _animate.value = true

            }

            override fun onFailed(result: AuthResult<LoggedInUser>) {
                if(result is AuthResult.Error){
                    _loginResult.value = LoginResult(message = result.exception.message)
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