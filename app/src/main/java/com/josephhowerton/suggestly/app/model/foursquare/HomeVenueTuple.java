package com.josephhowerton.suggestly.app.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.josephhowerton.suggestly.app.model.Suggestion;
import com.josephhowerton.suggestly.utility.SuggestionType;

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
