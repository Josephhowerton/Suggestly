package com.mortonsworld.suggestly.retrofit;

import com.mortonsworld.suggestly.utility.Config;

import java.util.HashMap;

public class NewYorkTimesManager {

    public static final String CLIENT_KEY_PARAM = "api-key";

    private static final String LIST_PARAM = "list";
    private static final String VERSION = "20201001";


    private NewYorkTimesManager(){}

    public static HashMap<String, String> buildQueryMap(String bestsellerList){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_KEY_PARAM, Config.CLIENT_KEY_VALUE);
        searchMap.put(LIST_PARAM, bestsellerList);

        return searchMap;
    }

    public static HashMap<String, String> buildQueryMap(){
        HashMap<String, String> searchMap = new HashMap<>();

        searchMap.put(CLIENT_KEY_PARAM, Config.CLIENT_KEY_VALUE);

        return searchMap;
    }
}