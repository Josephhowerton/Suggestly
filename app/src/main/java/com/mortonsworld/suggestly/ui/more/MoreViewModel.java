package com.mortonsworld.suggestly.ui.more;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.user.LocationTuple;

import java.util.List;

public class MoreViewModel extends AndroidViewModel {
    private final Repository repository;

    public MoreViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<LocationTuple> initUserLocation(){
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public LiveData<List<Suggestion>> initRecommendedVenues(){
        return repository.readRecommendedVenuesLiveData();
    }

    public LiveData<List<Suggestion>> initVenues(String id){
        return repository.readVenuesUsingCategoryIdLiveData(id);
    }

    public LiveData<List<Suggestion>> initBooksByListName(String listName){
        return repository.readNewYorkTimesBestsellingListLiveData(listName);
    }
    public LiveData<Boolean> fetchVenues(double lat, double lng, String id){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, id);
    }
}
