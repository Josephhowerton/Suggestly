package com.mortonsworld.suggestly.ui.welcome;

import androidx.databinding.DataBindingUtil;

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

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.FragmentWelcomeBinding;
import com.mortonsworld.suggestly.utility.NetworkHandler;

import java.io.IOException;

public class WelcomeFragment extends Fragment implements View.OnClickListener ,
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener{
    private final String MESSAGE = "In order to continue, please check network connection and try again.";
    private FragmentWelcomeBinding binding;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        initBackgroundVideo();
        initGettingStartedButton();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    public void initGettingStartedButton(){
        binding.gettingStartedButton.setOnClickListener(this);
    }

    public void initBackgroundVideo(){
        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();
    }

    @Override
    public void onClick(View view) {
        if(NetworkHandler.isNetworkConnectionActive(requireActivity())){
            crossFadeOutAnimation();
        }else{
            NetworkHandler.notifyBadConnectionAndDismiss(requireActivity(), MESSAGE);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {

            Uri uri = Uri.parse("android.resource://"+requireActivity().getPackageName()+"/"+R.raw.getting_started_video);
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
                if(binding.backgroundVideo != null){
                    binding.backgroundVideo.setSurfaceTextureListener(null);
                }
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_location_services);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }
}