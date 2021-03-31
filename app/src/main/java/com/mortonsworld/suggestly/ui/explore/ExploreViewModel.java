package com.mortonsworld.suggestly.ui.explore;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.auth.FirebaseAuth;
import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.relations.SearchTuple;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.utility.Config;

import java.util.List;

public class ExploreViewModel extends AndroidViewModel {
    private final Repository repository;
    private final PagedList.Config config;
    public LocationTuple location;
    public ExploreViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        location = repository.getLastFetchedLocation(0.0, 0.0);
        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(50)
                .setEnablePlaceholders(true)
                .build();
    }


    public LiveData<PagedList<SearchTuple>> suggestlySearch(String query) {
        return new LivePagedListBuilder<>(repository.suggestlySearch(query), config).build();
    }

    public void initializeVenueSearch(){
        repository.initializeVenueSearch(location.lat, location.lng);
    }

}