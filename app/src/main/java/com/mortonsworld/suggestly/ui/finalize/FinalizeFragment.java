package com.mortonsworld.suggestly.ui.finalize;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.model.user.User;
import com.mortonsworld.suggestly.ui.main.MainActivity;
import com.mortonsworld.suggestly.utility.DistanceCalculator;

public class FinalizeFragment extends Fragment{

    private FinalizeViewModel mViewModel;
    private boolean isRECOMMENDEDCompleted = false;
    private boolean isFOODCompleted = false;
    private boolean isBREWERYCompleted = false;
    private boolean isFAMILYFUNCompleted = false;
    private boolean isEVENTSCompleted = false;
    private boolean isACTIVECompleted = false;
    private boolean isSOCIALCompleted = false;
    private boolean isENTERTAINMENTCompleted = false;

    public static FinalizeFragment newInstance() {
        return new FinalizeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finalize, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FinalizeViewModel.class);
        mViewModel.getUserLocationLiveData().observe(getViewLifecycleOwner(), user -> {
            if(DistanceCalculator.hasValidLocation(user.getLat(), user.getLng())){
                getFoursquareVenuesNearUser_RECOMMENDED(user);
                getFoursquareVenuesNearUser_FOOD(user);
                getFoursquareVenuesNearUser_BREWERY(user);
                getFoursquareVenuesNearUser_FAMILY_FUN(user);
                getFoursquareVenuesNearUser_EVENTS(user);
                getFoursquareVenuesNearUser_ACTIVE(user);
                getGeneralFoursquareVenuesNearUserById_SOCIAL(user);
            }
        });
    }

    public void getFoursquareVenuesNearUser_RECOMMENDED(User user){
        mViewModel.getRecommendedFoursquareVenuesNearUser(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isRECOMMENDEDCompleted = true;
            goToMainActivity();
        });
    }

    public void getFoursquareVenuesNearUser_FOOD(User user){
        mViewModel.getFoursquareVenuesNearUser_FOOD(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isFOODCompleted = true;
            goToMainActivity();
        });
    }

    public void getFoursquareVenuesNearUser_BREWERY(User user){
        mViewModel.getGeneralFoursquareVenuesNearUser_BREWERY(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isBREWERYCompleted = true;
            goToMainActivity();
        });
    }

    public void getFoursquareVenuesNearUser_FAMILY_FUN(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isFAMILYFUNCompleted = true;
            goToMainActivity();
        });
    }

    public void getFoursquareVenuesNearUser_EVENTS(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_EVENTS(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isEVENTSCompleted = true;
            goToMainActivity();
        });
    }

    public void getFoursquareVenuesNearUser_ACTIVE(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_ACTIVE(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isACTIVECompleted = true;
            goToMainActivity();
        });
    }

    public void getGeneralFoursquareVenuesNearUserById_SOCIAL(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_SOCIAL(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isSOCIALCompleted = true;
            goToMainActivity();
        });
    }

    public void goToMainActivity(){
        if(isRECOMMENDEDCompleted && isFOODCompleted && isBREWERYCompleted && isFAMILYFUNCompleted && isEVENTSCompleted && isACTIVECompleted && isSOCIALCompleted){
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent);
            requireActivity().finish();
        }

    }
}