package com.mortonsworld.suggest.model.foursquare;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CategoryWithVenues {
    @Embedded
    public FoursquareCategory category;

    @Relation(parentColumn = "category_id", entityColumn = "id", associateBy = @Junction(FoursquareVenueCategoryCrossRef.class))
    public List<FoursquareVenue> venues;
}
