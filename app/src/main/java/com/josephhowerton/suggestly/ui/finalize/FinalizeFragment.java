package com.josephhowerton.suggestly.ui.finalize;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import com.josephhowerton.suggestly.model.user.User;
import com.josephhowerton.suggestly.ui.main.MainActivity;
import com.josephhowerton.suggestly.utility.DistanceCalculator;
import com.josephhowerton.suggestly.utility.NetworkHandler;

import java.io.IOException;

public class FinalizeFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private FragmentFinalizeBinding binding;
    private FinalizeViewModel mViewModel;
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

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FinalizeViewModel.class);
        mViewModel.getUserLocationLiveData().observe(getViewLifecycleOwner(), user -> {
            if(DistanceCalculator.hasValidLocation(user.getLat(), user.getLng())){
                binding.loadingLayout.message.setText(R.string.title_finding_suggestions);
                storeLastFetchedLocation(user.getLat(), user.getLng());
                getFoursquareVenuesNearUser_RECOMMENDED(user);
                getFoursquareVenuesNearUser_FOOD(user);
                getFoursquareVenuesNearUser_BREWERY(user);
                getFoursquareVenuesNearUser_FAMILY_FUN(user);
                getFoursquareVenuesNearUser_EVENTS(user);
                getFoursquareVenuesNearUser_ACTIVE(user);
                getFoursquareVenuesNearUser_SOCIAL(user);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!NetworkHandler.isNetworkConnectionActive(requireActivity())){
            NetworkHandler.notifyBadConnectionAndTerminate(requireActivity());
        }
    }

    public void storeLastFetchedLocation(double lat, double lng){
        mViewModel.storeLastFetchedLocation(lat, lng);
    }


    public void getFoursquareVenuesNearUser_RECOMMENDED(User user){
        mViewModel.getRecommendedFoursquareVenuesNearUser(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isRECOMMENDEDCompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_FOOD(User user){
        mViewModel.getFoursquareVenuesNearUser_FOOD(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isFOODCompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_BREWERY(User user){
        mViewModel.getGeneralFoursquareVenuesNearUser_BREWERY(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isBREWERYCompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_FAMILY_FUN(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isFAMILYFUNCompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_EVENTS(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_EVENTS(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isEVENTSCompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_ACTIVE(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_ACTIVE(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isACTIVECompleted = true;
            navigate();
        });
    }

    public void getFoursquareVenuesNearUser_SOCIAL(User user){
        mViewModel.getGeneralFoursquareVenuesNearUserById_SOCIAL(user.getLat(), user.getLng()).observe(requireActivity(), aBoolean -> {
            isSOCIALCompleted = true;
            navigate();
        });
    }

    public void navigate(){
        if(isRECOMMENDEDCompleted && isFOODCompleted && isBREWERYCompleted && isFAMILYFUNCompleted && isEVENTSCompleted && isACTIVECompleted && isSOCIALCompleted){
            crossFadeOutAnimation();
        }
    }

    public void navigateToPush(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_push);
    }

    public void goToMainActivity(){
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().startActivity(intent);
        requireActivity().finish();
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
            Surface surface = new Surface(surfaceTexture);
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