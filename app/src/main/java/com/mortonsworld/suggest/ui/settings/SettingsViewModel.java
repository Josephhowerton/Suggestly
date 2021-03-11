package com.mortonsworld.suggest.ui.settings;

import android.Manifest;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.utility.PermissionManager;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SettingsViewModel extends AndroidViewModel {
    private Repository repository;
    private PermissionManager permissionManager;
    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        permissionManager = new PermissionManager(application);
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
        return repository.isLocationUpdatesActive();
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
