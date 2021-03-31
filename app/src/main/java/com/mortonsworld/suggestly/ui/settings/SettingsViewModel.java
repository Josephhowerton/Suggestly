package com.mortonsworld.suggestly.ui.settings;

import android.Manifest;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.utility.PermissionManager;
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
    }

    public LiveData<Boolean> deleteAccount(){
        return repository.deleteUser();
    }
}
