package com.mortonsworld.suggestly.retrofit;

import java.util.HashMap;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import io.reactivex.rxjava3.core.Observable;
import com.mortonsworld.suggestly.model.foursquare.CategoryResponse;
import com.mortonsworld.suggestly.model.foursquare.DetailsResponse;
import com.mortonsworld.suggestly.model.foursquare.RecommendationsResponse;
import com.mortonsworld.suggestly.model.foursquare.SearchResponse;
import com.mortonsworld.suggestly.model.foursquare.SimilarResponse;

public interface FoursquareService {

    @GET("/v2/venues/categories")
    Observable<CategoryResponse> getFoursquareCategories(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/search")
    Observable<SearchResponse> getFoursquareVenuesNearby(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/{venue_id}/similar")
    Observable<SimilarResponse> getSimilarFoursquareVenuesNearby(@Path("venue_id") String venueId, @QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/search/recommendations")
    Observable<RecommendationsResponse> getRecommendedFoursquareVenuesNearby(@QueryMap HashMap<String, String> argumentMap);

    @GET("/v2/venues/{venue_id}")
    Observable<DetailsResponse> getFoursquareVenueDetails(@Path("venue_id") String venueId, @QueryMap HashMap<String, String> argumentMap);

}
