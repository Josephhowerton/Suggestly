package com.mortonsworld.suggestly.model.foursquare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;

import com.mortonsworld.suggestly.model.Suggestion;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj.getClass() != VenueAndCategory.class){
            return false;
        }else{
            Venue temp = ((VenueAndCategory) obj).venue;
            if(venue != null){
                return temp.venueId.equals(getId()) && temp.isSaved == venue.isSaved;
            }
            else{
                return false;
            }
        }
    }
}
