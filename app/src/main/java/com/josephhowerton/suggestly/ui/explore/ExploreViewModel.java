package com.josephhowerton.suggestly.ui.explore;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.josephhowerton.suggestly.Repository;
import com.josephhowerton.suggestly.model.relations.SearchTuple;
import com.josephhowerton.suggestly.model.user.LocationTuple;

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