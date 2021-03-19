package com.mortonsworld.suggestly.model.relations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.utility.SuggestionType;

public class VenueAndCategory extends Suggestion {

    @NonNull
    @Override
    public String getId() {
        return venue.venueId;
    }

    @NonNull
    @Override
    public SuggestionType getSuggestionType() {
        return SuggestionType.FOURSQUARE_VENUE;
    }

    @Embedded public Category category;
    @Embedded public Venue venue;
}
