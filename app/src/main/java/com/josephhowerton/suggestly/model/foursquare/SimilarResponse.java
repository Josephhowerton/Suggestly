package com.josephhowerton.suggestly.model.foursquare;

import java.util.List;

public class SimilarResponse {
    public SimilarResponse.Response response;

    public static class Response {
        public SimilarVenues similarVenues;
    }

    public static class SimilarVenues {
        public List<Venue> items;
    }
}
