package com.app.suggestly.ui.auth.register

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.app.suggestly.BuildConfig
import com.app.suggestly.R
import com.app.suggestly.databinding.FragmentRegisterBinding
import com.app.suggestly.ui.auth.auth.AuthViewModelFactory
import com.app.suggestly.ui.auth.signin.LoggedInUserView

class RegisterFragment : Fragment(), Animator.AnimatorListener  {
    private lateinit var binding:FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initTextWatcher()

        viewModel.signUpWithGoogle.observe(viewLifecycleOwner){
            if(it) signUpWithGoogle()
        }

        viewModel.registerFormState.observe(viewLifecycleOwner,
                Observer {registerFormState ->
                    if (registerFormState == null){
                        return@Observer
                    }

                    binding.btnEmailAuth.isEnabled = registerFormState.isDataValid

                    registerFormState.emailError?.let {
                        binding.editTextEmail.error = getString(it)
                    }

                    registerFormState.passwordError?.let {
                        binding.editTextPassword.error = getString(it)
                    }
                })

        viewModel.registerResult.observe(viewLifecycleOwner,
                Observer { registerResult ->
                    registerResult ?: return@Observer

                    binding.loading.visibility = View.GONE

                    registerResult.error?.let {
                        showRegisterFailed(it)
                    }

                    registerResult.message?.let {
                        showRegisterFailed(it)
                    }

                    registerResult.success?.let {
                        showRegisterSuccess(it)
                    }
                })
    }

    private fun init(){
        viewModel = ViewModelProvider(this, AuthViewModelFactory(requireActivity().application)).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) viewModel.signUpWithGoogle(result.data)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.FIREBASE_GOOGLE_CLIENT_ID)
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity().application, gso)

        viewModel.animate.observe(viewLifecycleOwner){
            animate()
        }
    }

    private fun signUpWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun animate(){
        ObjectAnimator.ofFloat(binding.imageViewClose, "translationY", 10000f)
                .apply {
                    duration = 750
                    start()
                }

        ObjectAnimator.ofFloat(binding.editTextName, "translationY", -20000f)
                .apply {
                    duration = 750
                    start()
                }

        ObjectAnimator.ofFloat(binding.editTextEmail, "translationX", 20000f)
                .apply {
                    duration = 550
                    startDelay = 200
                    start()
                }

        ObjectAnimator.ofFloat(binding.editTextPassword, "translationX", -20000f)
                .apply {
                    duration = 550
                    startDelay = 200
                    start()
                }

        ObjectAnimator.ofFloat(binding.btnEmailAuth, "translationX", 20000f)
                .apply {
                    duration = 550
                    startDelay = 250
                    start()
                }

//        ObjectAnimator.ofFloat(binding.btnGoogleAuth, "translationX", -20000f)
//                .apply {
//                    duration = 550
//                    startDelay = 250
//                    start()
//                }
//
//        ObjectAnimator.ofFloat(binding.textViewOr, "alpha", 0f)
//                .apply {
//                    duration = 550
//                    startDelay = 250
//                    start()
//                }
//
//        ObjectAnimator.ofFloat(binding.viewEnd, "alpha", 0f)
//                .apply {
//                    duration = 550
//                    startDelay = 250
//                    start()
//                }
//
//        ObjectAnimator.ofFloat(binding.viewStart, "alpha", 0f)
//                .apply {
//                    duration = 550
//                    startDelay = 250
//                    start()
//                }

        ObjectAnimator.ofFloat(binding.textViewSignIn, "alpha", 0f)
                .apply {
                    duration = 550
                    startDelay = 250
                    start()
                }.addListener(this)
    }

    private fun navigate(whereTo: Int){
        findNavController().navigate(whereTo)
    }


    override fun onAnimationStart(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {
        navigate(viewModel.destination!!)
    }

    override fun onAnimationCancel(animation: Animator?) {

    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

    private fun initTextWatcher(){
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                viewModel.registerDataChanged()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.registerDataChanged()
            }
        }

        binding.editTextName.addTextChangedListener(afterTextChangedListener)
        binding.editTextPassword.addTextChangedListener(afterTextChangedListener)
        binding.editTextPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.signUpWithEmail()
            }
            false
        }
    }

    private fun showRegisterSuccess(model: LoggedInUserView) {
        val welcome = "${getString(R.string.welcome)} ${model.displayName}"
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

}