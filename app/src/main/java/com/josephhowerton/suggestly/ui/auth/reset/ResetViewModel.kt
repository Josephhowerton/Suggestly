package com.josephhowerton.suggestly.ui.auth.reset

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.josephhowerton.suggestly.R
import com.josephhowerton.suggestly.app.Repository
import com.josephhowerton.suggestly.app.network.auth.AuthResult
import com.josephhowerton.suggestly.app.network.interfaces.ResetCompleteListener
import com.josephhowerton.suggestly.ui.auth.signin.LoginFormState

class ResetViewModel(private val repository: Repository) : ViewModel() {

    private val _resetFormState = MutableLiveData<LoginFormState>()
    val resetFormState: LiveData<LoginFormState> = _resetFormState

    private val _resetEmailResult = MutableLiveData<ResetEmail>()
    val resultEmail: LiveData<ResetEmail> = _resetEmailResult

    private val _animate = MutableLiveData<Boolean>()
    val animate: LiveData<Boolean>
        get() = _animate

    private val _isLoading = MutableLiveData<Int>()
    val isLoading: LiveData<Int> get() = _isLoading

    private val _btnVisible = MutableLiveData<Int>()
    val btnVisible: LiveData<Int> get() = _btnVisible

    private val _successLayoutVisible = MutableLiveData<Int>()
    val successLayoutVisible: LiveData<Int> get() = _successLayoutVisible

    var destination:Int? = null

    var email:String = ""

    init {
        _isLoading.value = View.GONE
        _btnVisible.value = View.VISIBLE
        _successLayoutVisible.value = View.GONE
        _resetFormState.value = LoginFormState(isDataValid = false)
    }

    fun onNavigateBack(view: View){
        destination = R.id.action_resetFragment_to_signInFragment
        _animate.value = true
    }

    fun onResetClick(view: View){
        _isLoading.value = View.VISIBLE
        repository.sendPasswordResetEmail(email, object : ResetCompleteListener {
            override fun onSuccess(result: AuthResult<ResetEmail>) {
                if (result is AuthResult.Success) {
                    _resetEmailResult.value = ResetEmail(success = result.data.success)
                    _successLayoutVisible.value = View.VISIBLE
                    _btnVisible.value = View.GONE
                }
                _isLoading.value = View.GONE
            }

            override fun onFailed(result: AuthResult<ResetEmail>) {
                if (result is AuthResult.Error) {
                    _resetEmailResult.value = ResetEmail(message = result.exception.message)
                } else {
                    _resetEmailResult.value = ResetEmail(error = R.string.reset_password_failed)
                }
                _isLoading.value = View.GONE
            }
        })
    }

    fun loginDataChanged() {
        if (!isEmailValid()) {
            _resetFormState.value = LoginFormState(emailError = R.string.invalid_username)
        }
        else {
            _resetFormState.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }
}