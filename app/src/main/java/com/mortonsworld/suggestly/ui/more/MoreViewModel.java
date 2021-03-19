package com.mortonsworld.suggestly.ui.more;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;

import java.util.List;

public class MoreViewModel extends AndroidViewModel {
    private final Repository repository;

    public MoreViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<Category> getFoursquareCategoryName(String id){
        return repository.getFoursquareCategoryName(id);
    }

    public LiveData<LocationTuple> initUserLocation(){
        return repository.readUserLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

}
