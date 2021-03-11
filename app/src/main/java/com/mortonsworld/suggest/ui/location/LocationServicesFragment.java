package com.mortonsworld.suggest.ui.location;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.databinding.FragmentLocationServicesBinding;
import com.mortonsworld.suggest.utility.Config;
import com.mortonsworld.suggest.utility.PermissionManager;

public class LocationServicesFragment extends Fragment implements View.OnClickListener{
    private FragmentLocationServicesBinding binding;
    private LocationServicesViewModel mViewModel;
    private boolean navigatedToSettings = false;

    public static LocationServicesFragment newInstance() {
        return new LocationServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_services, container, false);

        binding.manualLocationText.setOnClickListener(this);

        Button enable = binding.enableButton;
        enable.setOnClickListener(this);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LocationServicesViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();

        if(navigatedToSettings){
            checkLocationPermission();
            navigatedToSettings = false;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.enableButton){
            checkLocationPermission();
            return;
        }

        if(view.getId() == R.id.manual_location_text){
            disableLocationServices();
            navigateToManualLocationFragment();
        }
    }

    private void enableLocationServices(){
        mViewModel.enableLocationServices();
    }

    private void disableLocationServices(){
        mViewModel.disableLocationServices();
    }

    private void checkLocationPermission(){
        mViewModel.checkLocationPermission(requireContext(), new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                requestPermissions(new String[]
                                {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        Config.LOCATION_REQUEST_CODE);
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                showPermissionPreviouslyDeniedRationale();
            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                showPermissionPreviouslyDeniedWithNeverAskAgainRationale();
            }

            @Override
            public void onPermissionGranted() {
                navigateToFinalizeFragment();
                enableLocationServices();
            }
        });
    }

    private void showPermissionPreviouslyDeniedRationale(){
        new AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Suggest uses your location to curate experiences near you. Are your sure you want to deny permission.")
                .setCancelable(false)
                .setNegativeButton("TRY AGAIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, Config.LOCATION_REQUEST_CODE);
                })
                .setPositiveButton("DENY", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    showLocationManualMode();
                })
                .show();
    }

    private void showPermissionPreviouslyDeniedWithNeverAskAgainRationale(){
        new AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Suggest uses your location to curate unique experiences near you. Navigate to settings and turn on location.")
                .setCancelable(false)
                .setNegativeButton("LATER", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    navigateToManualLocationFragment();
                })
                .setPositiveButton("SETTINGS", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    goToSettings();
                })
                .show();
    }

    private void showLocationManualMode(){
        new AlertDialog.Builder(requireContext()).setTitle("Permission Skipped")
                .setMessage("Suggest uses your location to curate unique experiences near you. However we understand your decision. Would you like to enter a location manually?")
                .setCancelable(false)
                .setNegativeButton("LATER", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setPositiveButton("ENTER", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    navigateToManualLocationFragment();
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Config.LOCATION_REQUEST_CODE){
            mViewModel.checkLocationPermissionStatus(requireActivity(), new PermissionManager.LocationPermissionListener() {
                @Override
                public void onLocationPermissionGranted() {
                    enableLocationServices();
                    navigateToFinalizeFragment();
                }

                @Override
                public void onLocationPermissionDenied() {
                    disableLocationServices();
                    checkLocationPermission();
                }
            });
        }
    }

    public void navigateToFinalizeFragment(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_finalize);
    }

    public void navigateToManualLocationFragment(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_manual_location);
    }

    public void goToSettings(){
        navigatedToSettings = true;
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + requireActivity().getPackageName());
        intent.setData(uri);
        startActivityForResult(intent, Config.LOCATION_REQUEST_CODE);
    }
}