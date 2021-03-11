package com.mortonsworld.suggest.ui.finalize;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.model.user.User;
import com.mortonsworld.suggest.utility.Config;
import com.google.firebase.auth.FirebaseAuth;

public class FinalizeViewModel extends AndroidViewModel {
    private Repository repository;

    public FinalizeViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<User> getUserLocationLiveData(){
        return repository.readUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
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