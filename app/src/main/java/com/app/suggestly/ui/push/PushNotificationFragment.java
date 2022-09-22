package com.app.suggestly.ui.push;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.suggestly.R;
import com.app.suggestly.databinding.FragmentPushNotificationBinding;
import com.app.suggestly.ui.main.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PushNotificationFragment extends Fragment implements View.OnClickListener,
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener{

    private MediaPlayer mediaPlayer;
    private PushNotificationViewModel mViewModel;
    private FragmentPushNotificationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_push_notification, container, false);

        binding.skip.setOnClickListener(view -> goToMainActivity());
        binding.enableButton.setOnClickListener(this);

        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PushNotificationViewModel.class);
        mViewModel.updatePushNotificationPermission();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    @Override
    public void onClick(View v) {
        binding.loadingLayout.loadingLayout.setVisibility(View.VISIBLE);
        binding.enableButton.setVisibility(View.GONE);
        if(binding.skip.getId() == v.getId()){
            mViewModel.disablePushNotifications();
        }
        else if(binding.enableButton.getId() == R.id.enableButton){
            mViewModel.enablePushNotifications(requireActivity().getApplication());
        }
        crossFadeOutAnimation();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            Uri uri = Uri.parse("android.resource://"+requireActivity().getPackageName()+"/"+R.raw.push_notification_video);
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
    public void onPrepared(@NotNull MediaPlayer mediaPlayer) {
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
                goToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }

    public void goToMainActivity(){
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().startActivity(intent);
        requireActivity().finish();
    }

}