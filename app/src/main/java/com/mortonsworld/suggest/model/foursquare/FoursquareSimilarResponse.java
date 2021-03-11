package com.mortonsworld.suggest.model.foursquare;

import java.util.List;

public class FoursquareSimilarResponse {
    public FoursquareSimilarResponse.Response response;

    public static class Response {
        public SimilarVenues similarVenues;
    }

    public static class SimilarVenues {
        public List<FoursquareVenue> items;
    }
}
