package com.mortonsworld.suggest.model.foursquare;

import java.util.List;

public class FoursquareSearchResponse {
    public Response response;

    public static class Response {
        public List<FoursquareVenue> venues;
    }
}
