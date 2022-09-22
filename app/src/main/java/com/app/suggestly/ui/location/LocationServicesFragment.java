package com.app.suggestly.ui.location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.app.suggestly.R;
import com.app.suggestly.databinding.FragmentLocationServicesBinding;
import com.app.suggestly.utility.Config;
import com.app.suggestly.utility.PermissionManager;

import java.io.IOException;

public class LocationServicesFragment extends Fragment implements View.OnClickListener,
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener{

    private MediaPlayer mediaPlayer;

    private FragmentLocationServicesBinding binding;
    private LocationServicesViewModel mViewModel;
    private boolean navigatedToSettings = false;

    private Surface surface;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_services, container, false);

        binding.manualLocationText.setOnClickListener(this);

        Button enable = binding.enableButton;
        enable.setOnClickListener(this);

        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();

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

        if(mediaPlayer != null){
            mediaPlayer.start();
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

    @VisibleForTesting
    private void enableLocationServices(){
        mViewModel.enableLocationServices();
        mViewModel.observeLocationData().observe(this, isEnabled -> {
            if(isEnabled){
                navigateToFinalizeFragment();
            }else{
                navigateToManualLocationFragment();
            }
        });
    }

    @VisibleForTesting
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
                crossFadeOutAnimation();
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

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {

            Uri uri = Uri.parse("android.resource://"+requireActivity().getPackageName()+"/"+R.raw.location_updates_background_video);
            surface = new Surface(surfaceTexture);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(surface);
            mediaPlayer.setDataSource(requireContext(), uri);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepare();
        } catch (IOException e) {
            binding.backgroundVideo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return surfaceTexture.isReleased();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    private void crossFadeInAnimation(){
        AlphaAnimation animation = new AlphaAnimation(0.f, 1.f);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(1500);
        binding.backgroundVideo.setAnimation(animation);
    }

    private void crossFadeOutAnimation(){
        binding.backgroundVideo.clearAnimation();
        AlphaAnimation animation = new AlphaAnimation(1.f, 0.f);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                surface.release();
                binding.backgroundVideo.setSurfaceTextureListener(null);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_finalize);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }

}