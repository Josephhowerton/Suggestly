package com.mortonsworld.suggestly.model.foursquare;

import java.util.ArrayList;

public class CategoryResponse {
    public Response response;

    public static class Response {
        public ArrayList<Category> categories;
    }
}
