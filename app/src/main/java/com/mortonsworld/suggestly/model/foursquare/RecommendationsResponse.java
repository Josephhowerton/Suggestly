package com.mortonsworld.suggestly.model.foursquare;

import java.util.List;

public class RecommendationsResponse {
    public Response response;

    public static class Response {
        public Group group;
    }

    public static class Group{
        public List<FoursquareResult> results;
    }
}
