package com.app.suggestly.ui.finalize;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.user.LocationTuple;
import com.app.suggestly.app.model.user.User;
import com.app.suggestly.utility.Config;
import com.google.firebase.auth.FirebaseAuth;

public class FinalizeViewModel extends AndroidViewModel {
    private final Repository repository;
    private final MutableLiveData<User> _userLocationLiveData;

    public FinalizeViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        _userLocationLiveData = new MutableLiveData<User>();
    }

    public Boolean hasAskForPushNotificationsPreviously() {
        return repository.checkHasAskedForPushNotificationsPreviously();
    }

    public LiveData<LocationTuple> getUserLocationLiveData() {
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void storeLastFetchedLocation(double lat, double lng) {
        repository.storeLastFetchedLocation(lat, lng);
    }

    public LiveData<User> getUserLiveData() {
        return _userLocationLiveData;
    }

    public LiveData<Boolean> getRecommendedFoursquareVenuesNearUser(double lat, double lng) {
        return repository.getRecommendedFoursquareVenuesNearUser(lat, lng);
    }

    public LiveData<Boolean> getFoursquareVenuesNearUser_FOOD(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FOOD);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUser_BREWERY(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.BREWERY);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FAMILY_FUN);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_EVENTS(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.EVENTS);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_ACTIVE(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.ACTIVE);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_SOCIAL(double lat, double lng) {
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.SOCIAL);
    }
}