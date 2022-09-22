package com.app.suggestly.ui.auth.reset

import android.animation.Animator
import android.animation.ObjectAnimator
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.app.suggestly.R
import com.app.suggestly.databinding.FragmentResetBinding
import com.app.suggestly.ui.auth.auth.AuthViewModelFactory

class ResetFragment : Fragment(), Animator.AnimatorListener {
    private lateinit var binding: FragmentResetBinding
    private lateinit var viewModel: ResetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset, container, false)

        init()

        return binding.root
    }

    private fun init(){
        binding.lifecycleOwner = viewLifecycleOwner
        val factory = AuthViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(ResetViewModel::class.java)

        viewModel = ViewModelProvider(this, AuthViewModelFactory(requireActivity().application))
                            .get(ResetViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initTextWatcher()

        viewModel.resultEmail.observe(viewLifecycleOwner,  Observer { resetResult ->
            resetResult ?: return@Observer

            resetResult.error?.let {
                showResetFailed(it)
            }

            resetResult.message?.let {
                showResetFailed(it)
            }

            resetResult.success?.let {
                showResetSuccess(it)
            }
        })

        viewModel.resetFormState.observe(viewLifecycleOwner, Observer
        {registerFormState ->

            if (registerFormState == null){
                return@Observer
            }

            binding.btnResetPassword.isEnabled = registerFormState.isDataValid

            registerFormState.emailError?.let {
                binding.editTextEmail.error = getString(it)
            }
        }
        )

        viewModel.animate.observe(viewLifecycleOwner, {
            animate()
        })
    }

    private fun initTextWatcher(){
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                viewModel.loginDataChanged()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged()
            }
        }

        binding.editTextEmail.addTextChangedListener(afterTextChangedListener)
    }


    private fun animate(){
        ObjectAnimator.ofFloat(binding.imageViewClose, "translationY", 10000f)
                .apply {
                    println("imageViewClose")
                    duration = 750
                    start()
                }

        ObjectAnimator.ofFloat(binding.textViewGreeting, "translationX", -20000f)
                .apply {
                    println("textViewGreeting")
                    duration = 750
                    start()
                }

        ObjectAnimator.ofFloat(binding.editTextEmail, "translationX", 20000f)
                .apply {
                    println("editTextEmail")
                    duration = 550
                    startDelay = 200
                    start()
                }

        ObjectAnimator.ofFloat(binding.btnResetPassword, "translationY", 20000f)
                .apply {
                    println("btnResetPassword")
                    duration = 550
                    startDelay = 200
                    start()
                }.addListener(this)
    }

    private fun Int?.navigate(){
        println("Int?.navigate()")
        if(this != null) findNavController().navigate(this)
    }

    private fun showResetSuccess(success: Boolean) {
        val welcome = "${getString(R.string.reset)} ${success}"
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showResetFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showResetFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onAnimationEnd(animation: Animator?) {
        println("onAnimationEnd")
        R.id.navigation_sign_in.navigate()
    }

    override fun onAnimationStart(animation: Animator?) {}
    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}
}