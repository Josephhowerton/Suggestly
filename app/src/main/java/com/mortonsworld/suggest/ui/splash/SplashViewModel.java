package com.mortonsworld.suggest.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.model.user.LocationTuple;
import com.mortonsworld.suggest.model.user.User;
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
        return repository.readUserLocation(user.getId());
    }

    public LiveData<Boolean> checkIfUserInRoom(User user){
        return repository.checkIfUserInRoom(user.getId());
    }

    public LiveData<Boolean> isFoursquareTableFresh(double lat, double lng){
        return repository.isVenueTableFresh(lat, lng);
    }
}
