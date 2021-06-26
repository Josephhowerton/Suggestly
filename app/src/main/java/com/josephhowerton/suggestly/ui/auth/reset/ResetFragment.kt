package com.josephhowerton.suggestly.ui.auth.reset

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.josephhowerton.suggestly.R

class ResetFragment : Fragment() {

    companion object {
        fun newInstance() = ResetFragment()
    }

    private lateinit var viewModel: ResetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResetViewModel::class.java)
        // TODO: Use the ViewModel
    }

}