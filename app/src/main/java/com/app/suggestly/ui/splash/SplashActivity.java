package com.app.suggestly.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.app.suggestly.R;
import com.app.suggestly.app.model.user.User;
import com.app.suggestly.ui.init.InitializeActivity;
import com.app.suggestly.ui.main.MainActivity;
import com.app.suggestly.utility.Config;
import com.app.suggestly.utility.DistanceCalculator;

public class SplashActivity extends AppCompatActivity{
    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        authenticate();
    }


    public void authenticate(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            goToAuthActivity();
        } else {
            User user = new User(firebaseUser);
            checkIfUserInRoom(user);
        }
    }

    public void goToAuthActivity() {
        Intent intent = new Intent(this, InitializeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.INITIALIZE, Config.AUTHENTICATE_FLAG);
        startActivity(intent);
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToInitializeActivity() {
        Intent intent = new Intent(this, InitializeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.INITIALIZE, Config.INITIALIZE_USER);
        startActivity(intent);
        finish();
    }

    public void goToInitializeActivityRefreshSuggestions() {
        Intent intent = new Intent(this, InitializeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.INITIALIZE, Config.REFRESH_FLAG);
        startActivity(intent);
        finish();
    }

    public void goToInitializeActivityLocation() {
        Intent intent = new Intent(this, InitializeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.INITIALIZE, Config.LOCATION_FLAG);
        startActivity(intent);
        finish();
    }

    public void checkIfUserInRoom(User user) {
        viewModel.checkIfUserInRoom(user).observe(this, target -> {
            if (target) {
                readUserLocation(user);
            } else {
                createUser(user);
            }
        });
    }

    public void readUserLocation(User user) {
        viewModel.readUserLocation(user).observe(this, target -> {
            if (DistanceCalculator.hasValidLocation(target.lat, target.lng)) {
                updateDistance(target.lat, target.lng);
                isFoursquareTableFresh(target.lat, target.lng);
            } else {
                goToInitializeActivityLocation();
            }
        });
    }

    public void checkIfLocationServicesIsEnabled() {
        if (isLocationServicesEnabled()) {
            if (!isLocationUpdatesActive()) {
                try {
                    enableLocationServices();
                } catch (SecurityException securityException) {
                    Toast.makeText(this, R.string.message_location_services_denied, Toast.LENGTH_SHORT).show();
                    disableLocationServices();
                }
            }
        } else {
            disableLocationServices();
        }

        goToMainActivity();
    }

    /*
    checks if the location services ie location updates is enabled.
     */
    private Boolean isLocationServicesEnabled() {
        return viewModel.isLocationServicesEnabled();
    }

    private Boolean isLocationUpdatesActive() {
        return viewModel.isLocationUpdatesActive();
    }

    /*
    Enables location updates in the location source via the repository
     */
    private void enableLocationServices() {
        viewModel.enableLocationServices();
    }

    /*
    disables location updates in the location source via the repository and updates the value
    in shared preferences.
     */
    private void disableLocationServices() {
        if (isLocationServicesEnabled()) {
            viewModel.disableLocationServices();
        }
    }


    public void isFoursquareTableFresh(double lat, double lng) {
        viewModel.isFoursquareTableFresh(lat, lng).observe(this, isFresh -> {
            if (isFresh) {
                updateDistance(lat, lng);
            } else {
                goToInitializeActivityRefreshSuggestions();
            }
        });
    }

    public void createUser(User user) {
        viewModel.createUser(user).observe(this, target -> goToInitializeActivity());
    }

    public void updateDistance(Double lat, Double lng) {
        viewModel.updateDistance(lat, lng);
        checkIfLocationServicesIsEnabled();
    }
}