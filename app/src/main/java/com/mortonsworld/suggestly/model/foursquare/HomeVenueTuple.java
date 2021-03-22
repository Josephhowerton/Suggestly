package com.mortonsworld.suggestly.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.utility.SuggestionType;

public class HomeVenueTuple extends Suggestion {
    @NonNull
    @Override
    public String getId() {
        return venueId;
    }

    @NonNull
    @Override
    public SuggestionType getSuggestionType() {
        return SuggestionType.FOURSQUARE_VENUE;
    }

    @Embedded
    public Category category;

    public String venueId;
    public String address;
    public String city;
    public String state;
}
