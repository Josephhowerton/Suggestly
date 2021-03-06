package com.josephhowerton.suggestly.ui.finalize;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.josephhowerton.suggestly.app.Repository;
import com.josephhowerton.suggestly.app.model.user.User;
import com.josephhowerton.suggestly.utility.Config;
import com.google.firebase.auth.FirebaseAuth;

public class FinalizeViewModel extends AndroidViewModel {
    private final Repository repository;

    public FinalizeViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public Boolean hasAskForPushNotificationsPreviously(){
        return repository.checkHasAskedForPushNotificationsPreviously();
    }

    public LiveData<User> getUserLocationLiveData(){
        return repository.readUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void storeLastFetchedLocation(double lat, double lng){
        repository.storeLastFetchedLocation(lat, lng);
    }


    public LiveData<Boolean> getRecommendedFoursquareVenuesNearUser(double lat, double lng){
        return repository.getRecommendedFoursquareVenuesNearUser(lat, lng);
    }

    public LiveData<Boolean> getFoursquareVenuesNearUser_FOOD(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FOOD);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUser_BREWERY(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.BREWERY);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FAMILY_FUN);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_EVENTS(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.EVENTS);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_ACTIVE(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.ACTIVE);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_SOCIAL(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.SOCIAL);
    }
}