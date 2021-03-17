package com.mortonsworld.suggestly.model.relations;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;

public class VenueAndCategory {

    @Embedded public Category category;
    @Embedded public Venue venue;
}
