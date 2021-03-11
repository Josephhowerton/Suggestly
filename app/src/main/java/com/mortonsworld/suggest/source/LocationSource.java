package com.mortonsworld.suggest.source;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.HandlerThread;
import androidx.annotation.NonNull;

import com.mortonsworld.suggest.utility.Config;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class LocationSource extends FusedLocationProviderClient {
    private final BehaviorSubject<Location> locationUpdatesBehaviorSubject;
    private final HandlerThread locationSourceThread;
    private final LocationCallback locationCallback;
    private final LocationRequest locationRequest;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private Boolean isLocationRequestActive = false;

    @SuppressLint({"CommitPrefEdits", "VisibleForTests"})
    public LocationSource(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        locationUpdatesBehaviorSubject = BehaviorSubject.create();
        locationSourceThread = new HandlerThread("LocationSource");
        locationSourceThread.start();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                locationUpdatesBehaviorSubject.onNext(locationResult.getLastLocation());
            }
        };

        locationRequest = new LocationRequest();

        int TEN_SECONDS = 10000;
        int ONE_MILE = 100;
        locationRequest.setSmallestDisplacement(ONE_MILE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(TEN_SECONDS)
                .setFastestInterval(TEN_SECONDS);

    }

    public Boolean isLocationUpdatesActive(){
        return isLocationRequestActive;
    }

    public void subscribeToLocationUpdates(Observer<Location> locationObserver) {
        locationUpdatesBehaviorSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe(locationObserver);

    }

    public void unsubscribeToLocationUpdates() {
        removeLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        isLocationRequestActive = true;
        requestLocationUpdates(locationRequest, locationCallback, locationSourceThread.getLooper());
        enableLocationUpdates();
    }

    private void removeLocationUpdates() {
        isLocationRequestActive = false;
        removeLocationUpdates(locationCallback);
        disableLocationUpdates();
    }

    public Boolean isLocationEnabled(){
        return getValueFromSharedPreferences(Config.USER_SHARED_PREFERENCE_LOCATION_UPDATES, false);
    }

    public Boolean isLocationValid(){
        return getValueFromSharedPreferences(Config.USER_SHARED_PREFERENCE_VALID_LOCATION, false);
    }

    public void enableLocationUpdates(){
        updateSharedPreferences(Config.USER_SHARED_PREFERENCE_LOCATION_UPDATES, true);
    }

    public void disableLocationUpdates(){
        updateSharedPreferences(Config.USER_SHARED_PREFERENCE_LOCATION_UPDATES, false);
    }

    private void updateSharedPreferences(String key, Boolean target){
        editor.putBoolean(key, target);
        editor.apply();
    }

    private Boolean getValueFromSharedPreferences(String key, Boolean defaultValue){
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}

