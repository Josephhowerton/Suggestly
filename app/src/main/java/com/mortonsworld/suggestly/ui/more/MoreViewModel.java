package com.mortonsworld.suggestly.ui.more;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;

import java.util.List;

public class MoreViewModel extends AndroidViewModel {
    private final Repository repository;

    public MoreViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<List<VenueAndCategory>> initRecommendedVenues(){
        return repository.readRecommendedVenuesLiveData();
    }

    public LiveData<List<Suggestion>> initBooksByListName(String listName){
        return repository.readNewYorkTimesBestsellingListLiveData(listName);
    }

}
