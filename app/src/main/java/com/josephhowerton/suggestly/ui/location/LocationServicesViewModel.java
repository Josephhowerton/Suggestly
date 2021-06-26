package com.josephhowerton.suggestly.ui.location;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import com.josephhowerton.suggestly.app.Repository;
import com.josephhowerton.suggestly.utility.PermissionManager;

public class LocationServicesViewModel extends AndroidViewModel {
    private final PermissionManager permissionManager;
    private final Repository repository;
    public LocationServicesViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
        permissionManager = new PermissionManager(application);
    }

    public void checkLocationPermission(Context context, PermissionManager.PermissionAskListener listener){
        permissionManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, listener);
    }

    public void checkLocationPermissionStatus(Context context, PermissionManager.LocationPermissionListener listener){
        permissionManager.isLocationPermissionNeeded(context, Manifest.permission.ACCESS_FINE_LOCATION, listener);
    }

    public void enableLocationServices(){
        repository.enableLocationServices();
    }

    public void disableLocationServices(){
        repository.disableLocationServices();
    }

}