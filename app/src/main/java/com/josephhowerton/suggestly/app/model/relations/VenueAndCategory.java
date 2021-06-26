package com.josephhowerton.suggestly.app.model.relations;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.josephhowerton.suggestly.app.model.Suggestion;
import com.josephhowerton.suggestly.app.model.foursquare.Category;
import com.josephhowerton.suggestly.app.model.foursquare.Venue;
import com.josephhowerton.suggestly.utility.SuggestionType;

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
    public boolean equals(@NonNull Object obj) {
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
