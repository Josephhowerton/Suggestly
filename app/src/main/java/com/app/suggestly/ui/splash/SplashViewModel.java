package com.app.suggestly.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.user.LocationTuple;
import com.app.suggestly.app.model.user.User;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SplashViewModel extends AndroidViewModel {
    private final Repository repository;
    private final MutableLiveData<LocationTuple> _locationTuple;

    public SplashViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
        _locationTuple = new MutableLiveData<LocationTuple>();
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

//    public void fetchUserLocation(User user){
//        repository.readUserLocationLiveData(user.getId())
//                .subscribe(new Observer<LocationTuple>() {
//                    Disposable disposable;
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull LocationTuple locationTuple) {
//                        _locationTuple.setValue(locationTuple);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        disposable.dispose();
//                    }
//                });
//
//    }

    public LiveData<LocationTuple> readUserLocation(User user){
//        fetchUserLocation(user);
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
