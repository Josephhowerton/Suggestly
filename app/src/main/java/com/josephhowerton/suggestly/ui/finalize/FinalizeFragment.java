package com.josephhowerton.suggestly.ui.finalize;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.databinding.FragmentFinalizeBinding;
import com.josephhowerton.suggestly.app.model.user.User;
import com.josephhowerton.suggestly.utility.DistanceCalculator;
import com.josephhowerton.suggestly.utility.NetworkHandler;

import java.io.IOException;

public class FinalizeFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private FragmentFinalizeBinding binding;
    private FinalizeViewModel mViewModel;
    private Surface surface;
    private boolean isRECOMMENDEDCompleted = false;
    private boolean isFOODCompleted = false;
    private boolean isBREWERYCompleted = false;
    private boolean isFAMILYFUNCompleted = false;
    private boolean isEVENTSCompleted = false;
    private boolean isACTIVECompleted = false;
    private boolean isSOCIALCompleted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finalize, container, false);

        binding.loadingLayout.message.setText(R.string.title_finding_location);
        binding.loadingLayout.message.setTextSize(16);
        binding.loadingLayout.message.setTextColor(ResourcesCompat.getColor(getResources(), R.color.quantum_white_100, null));
        binding.loadingLayout.loadingLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.fui_transparent, null));

        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();
        fetchSuggestionsNearLocation();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!NetworkHandler.isNetworkConnectionActive(requireActivity())){
            NetworkHandler.notifyBadConnectionAndTerminate(requireActivity());
        }
    }

    public void fetchSuggestionsNearLocation(){
        startTimer();
        mViewModel = new ViewModelProvider(this).get(FinalizeViewModel.class);
        mViewModel.getUserLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if(DistanceCalculator.hasValidLocation(location.lat, location.lng)){
                binding.loadingLayout.message.setText(R.string.title_finding_suggestions);
                storeLastFetchedLocation(location.lat, location.lng);
                getFoursquareVenuesNearUser_RECOMMENDED(location.lat, location.lng);
                getFoursquareVenuesNearUser_FOOD(location.lat, location.lng);
                getFoursquareVenuesNearUser_BREWERY(location.lat, location.lng);
                getFoursquareVenuesNearUser_FAMILY_FUN(location.lat, location.lng);
                getFoursquareVenuesNearUser_EVENTS(location.lat, location.lng);
                getFoursquareVenuesNearUser_ACTIVE(location.lat, location.lng);
                getFoursquareVenuesNearUser_SOCIAL(location.lat, location.lng);
                crossFadeOutAnimation();
            }
        });
    }

    public void startTimer(){

    }

    public void storeLastFetchedLocation(double lat, double lng){
        mViewModel.storeLastFetchedLocation(lat, lng);
    }

    public void getFoursquareVenuesNearUser_RECOMMENDED(double lat, double lng){
        mViewModel.getRecommendedFoursquareVenuesNearUser(lat, lng).observe(requireActivity(), aBoolean -> {
            isRECOMMENDEDCompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_FOOD(double lat, double lng){
        mViewModel.getFoursquareVenuesNearUser_FOOD(lat, lng).observe(requireActivity(), aBoolean -> {
            isFOODCompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_BREWERY(double lat, double lng){
        mViewModel.getGeneralFoursquareVenuesNearUser_BREWERY(lat, lng).observe(requireActivity(), aBoolean -> {
            isBREWERYCompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_FAMILY_FUN(double lat, double lng){
        mViewModel.getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(lat, lng).observe(requireActivity(), aBoolean -> {
            isFAMILYFUNCompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_EVENTS(double lat, double lng){
        mViewModel.getGeneralFoursquareVenuesNearUserById_EVENTS(lat, lng).observe(requireActivity(), aBoolean -> {
            isEVENTSCompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_ACTIVE(double lat, double lng){
        mViewModel.getGeneralFoursquareVenuesNearUserById_ACTIVE(lat, lng).observe(requireActivity(), aBoolean -> {
            isACTIVECompleted = true;
        });
    }

    public void getFoursquareVenuesNearUser_SOCIAL(double lat, double lng){
        mViewModel.getGeneralFoursquareVenuesNearUserById_SOCIAL(lat, lng).observe(requireActivity(), aBoolean -> {
            isSOCIALCompleted = true;
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void navigate(){
        if(isRECOMMENDEDCompleted && isFOODCompleted && isBREWERYCompleted && isFAMILYFUNCompleted && isEVENTSCompleted && isACTIVECompleted && isSOCIALCompleted){
        }
    }

    public void navigateToPush(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_push);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mediaPlayer != null){
            mediaPlayer.start();
        }

    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            Uri uri = Uri.parse("android.resource://"+requireActivity().getPackageName()+"/"+R.raw.finalize_video);
            surface = new Surface(surfaceTexture);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(surface);
            mediaPlayer.setDataSource(requireContext(), uri);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setLooping(true);
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
        return false;
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
                navigateToPush();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }

}