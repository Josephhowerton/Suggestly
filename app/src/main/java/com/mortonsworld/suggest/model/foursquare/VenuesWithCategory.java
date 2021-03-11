package com.mortonsworld.suggest.model.foursquare;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class VenuesWithCategory {
    @Embedded public FoursquareVenue venue;

    @Relation(parentColumn = "id", entityColumn = "categoryId", associateBy = @Junction(FoursquareVenueCategoryCrossRef.class))
    public List<FoursquareCategory> categories;
}
