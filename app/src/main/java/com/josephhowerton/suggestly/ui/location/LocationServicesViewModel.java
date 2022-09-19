package com.josephhowerton.suggestly.ui.location;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.josephhowerton.suggestly.app.Repository;
import com.josephhowerton.suggestly.utility.PermissionManager;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class LocationServicesViewModel extends AndroidViewModel {
    private final PermissionManager permissionManager;
    private final Repository repository;

    private final MutableLiveData<Boolean> _enableLocationServices;

    public LocationServicesViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
        permissionManager = new PermissionManager(application);
        _enableLocationServices = new MutableLiveData<Boolean>();
    }

    public void checkLocationPermission(Context context, PermissionManager.PermissionAskListener listener){
        permissionManager.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, listener);
    }

    public void checkLocationPermissionStatus(Context context, PermissionManager.LocationPermissionListener listener){
        permissionManager.isLocationPermissionNeeded(context, Manifest.permission.ACCESS_FINE_LOCATION, listener);
    }

    @VisibleForTesting
    public void enableLocationServices(){
        repository.enableLocationServices()
                .subscribe(
                        new SingleObserver<Boolean> () {
                            private Disposable disposable;

                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onSuccess(@NonNull Boolean aBoolean) {
                                _enableLocationServices.setValue(aBoolean);
                                if(disposable != null){
                                    disposable.dispose();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                _enableLocationServices.setValue(false);
                                if(disposable != null){
                                    disposable.dispose();
                                }
                            }
                        }
                );
    }

    @VisibleForTesting
    public void disableLocationServices(){
        repository.disableLocationServices();
    }

    @VisibleForTesting
    public LiveData<Boolean> observeLocationData(){
        return _enableLocationServices;
    }

}