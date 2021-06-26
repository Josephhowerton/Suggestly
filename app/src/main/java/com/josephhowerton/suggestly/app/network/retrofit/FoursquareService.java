package com.josephhowerton.suggestly.app.network.retrofit;

import java.util.HashMap;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import io.reactivex.rxjava3.core.Observable;
import com.josephhowerton.suggestly.app.model.foursquare.CategoryResponse;
import com.josephhowerton.suggestly.app.model.foursquare.DetailsResponse;
import com.josephhowerton.suggestly.app.model.foursquare.RecommendationsResponse;
import com.josephhowerton.suggestly.app.model.foursquare.SearchResponse;
import com.josephhowerton.suggestly.app.model.foursquare.SimilarResponse;

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
