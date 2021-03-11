package com.mortonsworld.suggest.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.ui.splash.SplashActivity;
import com.mortonsworld.suggest.utility.Config;
import com.mortonsworld.suggest.utility.PermissionManager;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class PreferencesFragment extends PreferenceFragmentCompat implements PreferenceManager.OnPreferenceTreeClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "PreferencesFragment";
    private SettingsViewModel settingsViewModel;
    private final int RC_RE_AUTH_BEFORE_DELETE = 0;
    private boolean navigatedToSettings = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        getPreferenceManager().setSharedPreferencesName("DEFAULT");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        getPreferenceManager().setOnPreferenceTreeClickListener(this);
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(navigatedToSettings){
            checkLocationPermissions();
            navigatedToSettings = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if(preference.hasKey()){
            String selectedKey = preference.getKey();
            switch (selectedKey) {
                case Config.USER_SHARED_PREFERENCE_LOGOUT:
                    signCurrentUserOut();
                    break;
                case Config.USER_SHARED_PREFERENCE_DELETE:
                    deleteAccountConfirmationDialog();
                    break;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (Config.USER_SHARED_PREFERENCE_LOCATION_UPDATES.equals(s)) {
            configureLocationUpdates(sharedPreferences.getBoolean(s, true));
        }
    }

    private void configureLocationUpdates(boolean target){
        Log.println(Log.ASSERT, TAG, "configureLocationUpdates");
        if(target){
            checkLocationPermissions();
        }else{

        }
    }

    private void checkLocationPermissions(){
        Log.println(Log.ASSERT, TAG, "checkLocationPermissions");
        settingsViewModel.checkLocationPermissionStatus(new PermissionManager.LocationPermissionListener() {
            @Override
            public void onLocationPermissionGranted() {
                Log.println(Log.ASSERT, TAG, "checkLocationPermissions");
                if(settingsViewModel.isLocationUpdatesEnabled() && !settingsViewModel.isLocationUpdatesActive()){
                    settingsViewModel.enableLocationServices();
                }
            }

            @Override
            public void onLocationPermissionDenied() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showPermissionPreviouslyDeniedWithNeverAskAgainRationale();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_RE_AUTH_BEFORE_DELETE){
            if(resultCode == Activity.RESULT_OK){
                deleteCurrentUserRoom();
            }
        }
    }

    private void showPermissionPreviouslyDeniedWithNeverAskAgainRationale(){
        new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Suggest uses your location to curate unique experiences near you. Navigate to settings and turn on location.")
                .setCancelable(false)
                .setNegativeButton("LATER", (dialogInterface, i) -> {
                    settingsViewModel.disableLocationServices();
                })
                .setPositiveButton("SETTINGS", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    goToSettings();
                })
                .show();
    }

    private void signCurrentUserOut(){
        settingsViewModel.signOut();
        goToSplashActivity();
    }

    private void goToSplashActivity(){
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    public void goToSettings(){
        navigatedToSettings = true;
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + requireActivity().getPackageName());
        intent.setData(uri);
        startActivityForResult(intent, Config.LOCATION_REQUEST_CODE);
    }

    public void deleteAccountConfirmationDialog(){
        new AlertDialog.Builder(requireContext()).setTitle("Delete Your Account")
                .setMessage("Before deleting your account you must re-authenticate. Are your sure you want to continue?")
                .setCancelable(false)
                .setNegativeButton("CANCEL", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    goToAuthActivity();
                })
                .show();

    }

    public void goToAuthActivity(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_Suggest_NoActionBar)
                .setLogo(R.drawable.suggest_logo)
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_RE_AUTH_BEFORE_DELETE);
    }

    public void deleteCurrentUserRoom(){
        settingsViewModel.deleteAccount().observe(getViewLifecycleOwner(), wasSuccess -> {
            if(wasSuccess){
                new AlertDialog.Builder(requireContext()).setTitle("Account Deleted")
                        .setMessage("Your account was successfully deleted")
                        .setCancelable(false)
                        .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            goToSplashActivity();
                        })
                        .show();
            } else{
                new AlertDialog.Builder(requireContext()).setTitle("Account Deletion Error")
                        .setMessage("There was an issue deleting your account, please try again later.")
                        .setCancelable(false)
                        .setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            signCurrentUserOut();
                        })
                        .show();
            }
        });
    }

}