package com.mortonsworld.suggestly.callbacks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.foursquare.Venue;

public class VenueBoundaryCallback extends PagedList.BoundaryCallback<Venue> {
    private final Repository repository;
    private final String id;
    private final double lat;
    private final double lng;
    public VenueBoundaryCallback(Application application, String id, double lat, double lng) {
        repository = Repository.getInstance(application);
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public void onZeroItemsLoaded() {
        repository.getGeneralFoursquareVenuesNearUserById(lat, lng, id);
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull Venue itemAtFront) {
        repository.getGeneralFoursquareVenuesNearUserById(lat, lng, id);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Venue itemAtEnd) {
        repository.getGeneralFoursquareVenuesNearUserById(lat, lng, id);
    }
}
