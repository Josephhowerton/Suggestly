package com.josephhowerton.suggestly.app.network.retrofit;

import androidx.annotation.NonNull;

import com.josephhowerton.suggestly.BuildConfig;
import com.josephhowerton.suggestly.utility.Config;

import java.util.HashMap;

public class FoursquareManager {
    public static final String CLIENT_ID_FIELD = "client_id";
    public static final String CLIENT_SECRET_FIELD = "client_secret";
    private static final String LOCATION_PARAM = "ll";
    private static final String RADIUS_PARAM = "radius";
    private static final String QUERY_PARAM = "query";
    private static final String CATEGORY_PARAM = "categoryId";
    private static final String VERSIONING_PARAM = "v";

    private FoursquareManager(){}

    public static HashMap<String, String> buildSearchWithCoordinatesQueryMap(){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_ID_FIELD, BuildConfig.FOURSQUARE_CLIENT_ID);
        searchMap.put(CLIENT_SECRET_FIELD, BuildConfig.FOURSQUARE_CLIENT_SECRET);
        searchMap.put(VERSIONING_PARAM, Config.FOURSQUARE_VERSION);

        return searchMap;
    }

    public static HashMap<String, String> buildCoordinatesQueryMap(@NonNull Double lat, @NonNull Double lon){
        HashMap<String, String> searchMap = new HashMap<>();

        String coordinates = lat + "," + lon;
        searchMap.put(CLIENT_ID_FIELD, BuildConfig.FOURSQUARE_CLIENT_ID);
        searchMap.put(CLIENT_SECRET_FIELD, BuildConfig.FOURSQUARE_CLIENT_SECRET);
        searchMap.put(VERSIONING_PARAM, Config.FOURSQUARE_VERSION);
        searchMap.put(LOCATION_PARAM, coordinates);
        searchMap.put(RADIUS_PARAM, Config.RADIUS_DEFAULT_VALUE);

        return searchMap;
    }

    public static HashMap<String, String> buildSearchQueryMap(@NonNull Double lat, @NonNull Double lon, @NonNull String query){
        HashMap<String, String> searchMap = new HashMap<>();

        String coordinates = lat + "," + lon;
        searchMap.put(CLIENT_ID_FIELD, BuildConfig.FOURSQUARE_CLIENT_ID);
        searchMap.put(CLIENT_SECRET_FIELD, BuildConfig.FOURSQUARE_CLIENT_SECRET);
        searchMap.put(VERSIONING_PARAM, Config.FOURSQUARE_VERSION);
        searchMap.put(LOCATION_PARAM, coordinates);
        searchMap.put(RADIUS_PARAM, Config.RADIUS_DEFAULT_VALUE);
        searchMap.put(QUERY_PARAM, query);

        return searchMap;
    }

    public static HashMap<String, String> buildCategoryQueryMap(@NonNull Double lat, @NonNull Double lon, @NonNull String categoryId){
        HashMap<String, String> searchMap = new HashMap<>();

        String coordinates = lat + "," + lon;
        searchMap.put(CLIENT_ID_FIELD, BuildConfig.FOURSQUARE_CLIENT_ID);
        searchMap.put(CLIENT_SECRET_FIELD, BuildConfig.FOURSQUARE_CLIENT_SECRET);
        searchMap.put(VERSIONING_PARAM, Config.FOURSQUARE_VERSION);
        searchMap.put(LOCATION_PARAM, coordinates);
        searchMap.put(RADIUS_PARAM, Config.RADIUS_DEFAULT_VALUE);
        searchMap.put(CATEGORY_PARAM, categoryId);

        return searchMap;
    }
}
