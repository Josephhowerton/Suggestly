package com.app.suggestly.app.network.retrofit;

import com.app.suggestly.BuildConfig;

import java.util.HashMap;

public class GoogleManager {

    public static final String CLIENT_KEY_PARAM = "key";
    private static final String ADDRESS_PARAM = "address";

    private GoogleManager(){}

    public static HashMap<String, String> buildQueryMap(String address){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_KEY_PARAM, BuildConfig.GOOGLE_API_KEY);
        searchMap.put(ADDRESS_PARAM, address);

        return searchMap;
    }
}
