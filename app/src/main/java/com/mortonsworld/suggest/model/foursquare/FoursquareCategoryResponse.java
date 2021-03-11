package com.mortonsworld.suggest.model.foursquare;

import java.util.ArrayList;

public class FoursquareCategoryResponse {
    public Response response;

    public static class Response {
        public ArrayList<FoursquareCategory> categories;
    }
}
