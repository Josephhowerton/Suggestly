package com.josephhowerton.suggestly.app.model.foursquare;

import java.util.List;

public class SearchResponse {
    public Response response;

    public static class Response {
        public List<Venue> venues;
    }
}
