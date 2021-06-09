package com.josephhowerton.suggestly.ui.settings;

import android.Manifest;
import android.app.Application;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.josephhowerton.suggestly.BuildConfig;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.Repository;
import com.josephhowerton.suggestly.utility.PermissionManager;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SettingsViewModel extends AndroidViewModel {

    private final Repository repository;
    private final PermissionManager permissionManager;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        permissionManager = new PermissionManager(application);
    }
/* ***************************************************************************************
    Push Notifications
 *************************************************************************************** */

    public void updatePushNotificationsPreference(Application application, Boolean target){
        if(target){
            repository.enablePushNotifications(application);
        }
        else{
            repository.disablePushNotifications();
        }
    }


/* ***************************************************************************************
    Manual Location
 *************************************************************************************** */
    public LiveData<List<AutocompletePrediction>> subscribeToAutoCompleteResults(){
        return repository.subscribeToAddressAutoCompletePredictions();
    }

    public void searchManualLocationInput(String target){
        repository.fetchManualLocationInput(target);
    }

    public LiveData<Boolean> convertAddressToCoordinates(String target){
        return repository.convertManualLocationInputToCoordinates(target);
    }

/* ***************************************************************************************
    Manual Location
 *************************************************************************************** */
    public void checkLocationPermissionStatus(PermissionManager.LocationPermissionListener listener){
        permissionManager.isLocationPermissionNeeded(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION, listener);
    }

    public Boolean isLocationUpdatesEnabled(){
        return repository.isLocationServicesEnabled();
    }

    public void enableLocationServices(){
        repository.enableLocationServices();
    }

    public void disableLocationServices(){
        repository.disableLocationServices();
    }

/* ***************************************************************************************
    Manual Location
 *************************************************************************************** */
    public Boolean isLocationUpdatesActive(){
        return repository.isLocationUpdatesActive();
    }

    public void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.google_client_id)
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(getApplication(), options);

        client.signOut();

    }

    public LiveData<Boolean> deleteAccount(){
        return repository.deleteUser();
    }
}
