package com.josephhowerton.suggestly.app.model.relations;

import androidx.room.ColumnInfo;

public class SearchTuple {

    public String category_id;
    public String category_name;
    public String category_icon_prefix;
    public String category_icon_suffix;

    @ColumnInfo(name = "venue_name")
    public String name;

    @ColumnInfo(name = "id")
    public String venueId;

    public String getIconWithBGUrl(int size){
        if(category_icon_prefix != null && category_icon_suffix != null){
            return category_icon_prefix + "bg_" + size + category_icon_suffix;
        }
        return "";
    }
}
