package com.mortonsworld.suggest.retrofit;

import java.util.HashMap;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import io.reactivex.rxjava3.core.Observable;
import com.mortonsworld.suggest.model.foursquare.FoursquareCategoryResponse;
import com.mortonsworld.suggest.model.foursquare.FoursquareDetailsResponse;
import com.mortonsworld.suggest.model.foursquare.FoursquareRecommendationsResponse;
import com.mortonsworld.suggest.model.foursquare.FoursquareSearchResponse;
import com.mortonsworld.suggest.model.foursquare.FoursquareSimilarResponse;

public interface FoursquareService {

    @GET("/v2/venues/categories")
    Observable<FoursquareCategoryResponse> getFoursquareCategories(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/search")
    Observable<FoursquareSearchResponse> getFoursquareVenuesNearby(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/{venue_id}/similar")
    Observable<FoursquareSimilarResponse> getSimilarFoursquareVenuesNearby(@Path("venue_id") String venueId, @QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/search/recommendations")
    Observable<FoursquareRecommendationsResponse> getRecommendedFoursquareVenuesNearby(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/{venue_id}")
    Observable<FoursquareDetailsResponse> getFoursquareVenueDetails(@Path("venue_id") String venueId, @QueryMap HashMap<String, String> argumentMap);

}
