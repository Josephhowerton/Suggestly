package com.josephhowerton.suggestly.model.foursquare;

import java.util.List;

public class Photo {
    public Long count;
    public List<Group> groups;


    public static class Group{
        public String type;
        public String name;
        public Long count;
        public List<Item> items;
    }

    public static class Item{
        public String id;
        public long createdAt;
        public Source source;
        public String prefix;
        public String suffix;
        public Integer width;
        public Integer height;
        public User user;
        public String visibility;
    }

    public static class Source{
        public String name;
        public String url;
    }

    public static class User{
        public String id;
        public String firstName;
        public String lastName;
    }
}
