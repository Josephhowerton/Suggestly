package com.josephhowerton.suggestly.ui.auth.auth

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.josephhowerton.suggestly.BuildConfig
import com.josephhowerton.suggestly.R
import com.josephhowerton.suggestly.databinding.FragmentAuthBinding
import com.josephhowerton.suggestly.ui.auth.signin.LoggedInUserView
import com.josephhowerton.suggestly.ui.main.MainActivity
import java.util.*

class AuthFragment : Fragment(), Animator.AnimatorListener  {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentAuthBinding
    private lateinit var viewModel: AuthViewModel
    
    private  var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result  ->
                if (result.resultCode == Activity.RESULT_OK) viewModel.signInWithGoogle(result.data)
                else showLoginFailed("Error authenticating with Google.")
            }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)

        init()

        return binding.root
    }

    fun init() {
        val factory = AuthViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        signInEmailLiveData()
        signInGoogleLiveData()
        registerLiveData()
        registerResultLiveData()
        animateLiveData()
        navigateLiveData()

        resultLauncher

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.google_client_id)
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity().application, gso)

    }

    private fun animateLiveData() {
        viewModel.animate.observe(viewLifecycleOwner, {
            animate()
        })

    }

    private fun navigateLiveData() {
        viewModel.navigate.observe(viewLifecycleOwner, {
            viewModel.destination.navigate()
        })
    }

    private fun signInEmailLiveData() {
        viewModel.signInWithEmail.observe(viewLifecycleOwner, {
            viewModel.signInWithEmail(it)
        })
    }

    private fun signInGoogleLiveData() {
        viewModel.signInWithGoogle.observe(viewLifecycleOwner, {
            if(it) signInWithGoogle()
        })
    }

    private fun signInWithGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun foursquareTableLiveData() {
        viewModel.isFoursquareTableFresh.observe(viewLifecycleOwner, {
            if(it || viewModel.isNewUser) R.id.action_navigation_auth_to_navigation_explanation.navigate()
            else navigate()
        })
    }

    private fun registerLiveData() {
        viewModel.signUp.observe(viewLifecycleOwner, {
            viewModel.signUp(it)
        })
    }

    private fun registerResultLiveData(){
        viewModel.registerResult.observe(viewLifecycleOwner, Observer@ { loginResult ->
            loginResult ?: return@Observer

            loginResult.error?.let {
                showLoginFailed(it)
            }

            loginResult.message?.let {
                showLoginFailed(it)
            }

            loginResult.success?.let {
                showLoginSuccess(it)
            }
        })
    }

    private fun animate(){
        ObjectAnimator.ofFloat(binding.btnEmailAuth, "translationX", 20000f)
                .apply {
                    duration = 500
                    start()
                }

        ObjectAnimator.ofFloat(binding.btnGoogleAuth, "translationX", -20000f)
                .apply {
                    duration = 500
                    startDelay = 250
                    start()
                }

        ObjectAnimator.ofFloat(binding.textViewMessage, "alpha", 0f)
                .apply {
                    duration = 500
                    startDelay = 250
                    start()
                }

        ObjectAnimator.ofFloat(binding.textViewGreeting, "alpha", 0f)
                .apply {
                    duration = 500
                    startDelay = 250
                    start()
                }

        ObjectAnimator.ofFloat(binding.textViewSignUp, "alpha", 0f)
                .apply {
                    duration = 500
                    startDelay = 250
                    start()
                }
                .addListener(this)
    }

    override fun onAnimationStart(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
        when(viewModel.destination){
            R.id.action_navigation_auth_to_navigation_explanation -> foursquareTableLiveData()
            else -> viewModel.navigate()
        }
    }

    private fun Int?.navigate() {
        if(this != null) {
            findNavController().navigate(this)
        }
    }

    private fun navigate(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoginSuccess(model: LoggedInUserView) {
        val welcome = "${getString(R.string.welcome)} ${model.displayName}"
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

}
