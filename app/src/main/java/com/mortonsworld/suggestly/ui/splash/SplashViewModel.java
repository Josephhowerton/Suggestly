package com.mortonsworld.suggestly.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;
import com.firebase.ui.auth.IdpResponse;

public class SplashViewModel extends AndroidViewModel {
    private final Repository repository;

    public SplashViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<Boolean> authenticateUser(IdpResponse response){
        return repository.authenticateUser(response);
    }

    public LiveData<Boolean> createUser(User user){
        return repository.createUser(user);
    }

    public LiveData<LocationTuple> readUserLocation(User user){
        return repository.readUserLocationLiveData(user.getId());
    }

    public LiveData<Boolean> checkIfUserInRoom(User user){
        return repository.checkIfUserInRoom(user.getId());
    }

    public LiveData<Boolean> isFoursquareTableFresh(double lat, double lng){
        return repository.isVenueTableFresh(lat, lng);
    }
}
