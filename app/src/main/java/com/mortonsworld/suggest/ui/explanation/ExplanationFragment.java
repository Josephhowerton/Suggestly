package com.mortonsworld.suggest.ui.explanation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mortonsworld.suggest.R;

public class ExplanationFragment extends Fragment implements View.OnClickListener {

    private ExplanationViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        initGettingStartedButton(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExplanationViewModel.class);
        // TODO: Use the ViewModel
    }


    public void initGettingStartedButton(View view){
        Button gettingStartedButton = view.findViewById(R.id.getting_started_button);
        gettingStartedButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_location_services);
    }
}