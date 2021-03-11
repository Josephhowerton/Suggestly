package com.mortonsworld.suggest.model.foursquare;

import java.util.List;

public class FoursquareRecommendationsResponse {
    public Response response;

    public static class Response {
        public Group group;
    }

    public static class Group{
        public List<FoursquareResult> results;
    }
}
