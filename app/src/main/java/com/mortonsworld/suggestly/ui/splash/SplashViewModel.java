package com.mortonsworld.suggestly.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;
import com.firebase.ui.auth.IdpResponse;
import com.mortonsworld.suggestly.utility.Config;

public class SplashViewModel extends AndroidViewModel {
    private final Repository repository;

    public SplashViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<Boolean> isNewYorkTimesTableFresh(){
        return repository.isNewYorkTimesBooksTableFresh();
    }

    public void fetchNewYorkTimesBestsellingByListName(String list){
        repository.fetchNewYorkTimesBestsellingByListName(list);
    }

    public void updateDistance(double lat, double lng){
        repository.updateVenueDistance(lat, lng);
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

    public Boolean isLocationServicesEnabled(){
        return repository.isLocationServicesEnabled();
    }

    public Boolean isLocationUpdatesActive(){
        return repository.isLocationUpdatesActive();
    }

    public void enableLocationServices(){
        repository.enableLocationServices();
    }

    public void disableLocationServices(){
        repository.disableLocationServices();
    }
}
