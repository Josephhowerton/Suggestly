package com.mortonsworld.suggestly.model.foursquare;

import androidx.room.Embedded;
import androidx.room.Relation;

public class VenuesAndCategory {
    @Embedded Category category;

    @Relation(parentColumn = "category_id", entityColumn = "venue_id" )
    public Venue venue;
}
