package com.mortonsworld.suggest.retrofit;


import com.mortonsworld.suggest.model.google.GeocodeResponse;
import java.util.HashMap;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GoogleService {
    @GET("/maps/api/geocode/json")
    Observable<GeocodeResponse> fetchCoordinatesUsingAddress(@QueryMap HashMap<String, String> argumentMap);
}