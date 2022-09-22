package com.app.suggestly.app.network.retrofit;

import com.app.suggestly.BuildConfig;

import java.util.HashMap;

public class NewYorkTimesManager {

    public static final String CLIENT_KEY_PARAM = "api-key";
    private static final String LIST_PARAM = "list";


    private NewYorkTimesManager(){}

    public static HashMap<String, String> buildQueryMap(String bestsellerList){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_KEY_PARAM, BuildConfig.NEY_YORK_TIMES_KEY_VALUE);
        searchMap.put(LIST_PARAM, bestsellerList);

        return searchMap;
    }

    public static HashMap<String, String> buildQueryMap(){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_KEY_PARAM, BuildConfig.NEY_YORK_TIMES_KEY_VALUE);

        return searchMap;
    }
}