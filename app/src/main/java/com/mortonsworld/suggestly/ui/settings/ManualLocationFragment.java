package com.mortonsworld.suggestly.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import android.view.inputmethod.InputMethodManager;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.LocationAutoCompleteAdapter;
import com.mortonsworld.suggestly.databinding.FragmentManualLocationBinding;
import com.mortonsworld.suggestly.utility.NetworkHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ManualLocationFragment extends Fragment implements SearchView.OnQueryTextListener, LocationAutoCompleteAdapter.LocationSearchAdapterListener,
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    private FragmentManualLocationBinding binding;
    private SettingsViewModel settingsViewModel;
    private LocationAutoCompleteAdapter adapter;
    private final String MESSAGE = "Manual location uses the cloud to find your location. Please check internet connection and try again.";

    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_location, container, false);

        binding.backgroundVideo.setSurfaceTextureListener(this);
        crossFadeInAnimation();

        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setOnQueryTextListener(this);
        adapter = new LocationAutoCompleteAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        initializeSearchView();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!NetworkHandler.isNetworkConnectionActive(requireActivity())){
            new androidx.appcompat.app.AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.title_network_error))
                    .setMessage(String.format(getString(R.string.message_network_error_dismiss), MESSAGE))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.title_continue), (dialog, which) -> {
                        dialog.dismiss();
                        crossFadeOutAnimation();
                    }).show();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsViewModel.subscribeToAutoCompleteResults().observe(requireActivity(),
                autocompletePredictions -> adapter.setPredictions(autocompletePredictions));
    }

    private void initializeSearchView(){
        SearchView searchView = binding.searchView;
        searchView.setActivated(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        settingsViewModel.searchManualLocationInput(newText);
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

        if(requireActivity().getCurrentFocus() != null){
            InputMethodManager inputManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

        binding.loadingLayout.loadingLayout.setVisibility(View.VISIBLE);
        settingsViewModel.convertAddressToCoordinates(address).observe(requireActivity(), isSuccess -> {
            binding.loadingLayout.loadingLayout.setVisibility(View.GONE);
            if(isSuccess){
                showOnSuccessDialog();
            }else{
                showOnFailureDialog();
            }
        });
    }


    private void showOnSuccessDialog(){
        new AlertDialog.Builder(requireContext()).setTitle("Location Updated")
                .setMessage("Using your new location to find suggestions in your area.")
                .setCancelable(false)
                .setPositiveButton("CONTINUE", (dialog, which) -> {
                    crossFadeOutAnimation();
                }).show();
    }

    private void showOnFailureDialog(){
        new AlertDialog.Builder(requireContext()).setTitle("Location Update Failed")
                .setMessage( "This is embarrassing! We apologize for the error, try again later")
                .setCancelable(false)
                .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    crossFadeOutAnimation();
                })
                .show();
    }

    private void navigateBack(){
        requireActivity().getSupportFragmentManager()
                .popBackStack();
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
                binding.backgroundVideo.setSurfaceTextureListener(null);
                navigateBack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.backgroundVideo.setAnimation(animation);
    }
}