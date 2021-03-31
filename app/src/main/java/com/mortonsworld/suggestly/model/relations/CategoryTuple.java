package com.mortonsworld.suggestly.model.relations;

public class CategoryTuple {
    public String category_id;
    public String category_name;
    public String category_icon_prefix;
    public String category_icon_suffix;

    public String getIconUrl(){
        return category_icon_prefix + 64 + category_icon_suffix;
    }
}
