package com.app.suggestly.ui.location;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.app.suggestly.R;
import com.app.suggestly.adapter.LocationAutoCompleteAdapter;
import com.app.suggestly.databinding.FragmentManualLocationBinding;
import com.app.suggestly.utility.NetworkHandler;

import java.io.IOException;

public class ManualLocationFragment extends Fragment implements SearchView.OnQueryTextListener, LocationAutoCompleteAdapter.LocationSearchAdapterListener,
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    private FragmentManualLocationBinding binding;
    private ManualLocationViewModel mViewModel;
    private LocationAutoCompleteAdapter adapter;

    private MediaPlayer mediaPlayer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_location, container, false);

        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setOnQueryTextListener(this);
        adapter = new LocationAutoCompleteAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ManualLocationViewModel.class);
        mViewModel.listenForAutoCompleteResults().observe(requireActivity(),
                autocompletePredictions -> adapter.setPredictions(autocompletePredictions));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!NetworkHandler.isNetworkConnectionActive(requireActivity())){
            String MESSAGE = "Manual location uses the cloud to find your location. Please check internet connection and try again.";
            NetworkHandler.notifyBadConnectionAndGoBack(requireActivity(), MESSAGE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mediaPlayer != null){
            mediaPlayer.start();
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.searchManualLocationInput(newText);
        if(newText.isEmpty()){
            binding.recyclerView.setVisibility(View.GONE);
            binding.poweredByGoogle.setVisibility(View.GONE);
        }else{
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.poweredByGoogle.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onRowItemSelected(String address) {
        mViewModel.convertAddressToCoordinates(address).observe(requireActivity(), isSuccess -> {
            if(isSuccess){
                crossFadeOutAnimation();
            }else{
                showAddressConversionFailure();
            }
        });
    }

    private void showAddressConversionFailure(){
        new AlertDialog.Builder(requireContext()).setTitle("Failed to Convert to Geocode")
                .setMessage("Once an address is entered Suggest tries to geocode it. However there was an error during this conversion. Try Again?")
                .setCancelable(false)
                .setNegativeButton("LATER", (dialogInterface, i) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                    dialogInterface.dismiss();
                })
                .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    public void navigateToFinalizeFragment(){
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_finalize);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {

            Uri uri = Uri.parse("android.resource://"+requireActivity().getPackageName()+"/"+R.raw.manual_location_video);
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
                if(binding.backgroundVideo != null){
                    binding.backgroundVideo.setSurfaceTextureListener(null);
                }
                navigateToFinalizeFragment();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }
}