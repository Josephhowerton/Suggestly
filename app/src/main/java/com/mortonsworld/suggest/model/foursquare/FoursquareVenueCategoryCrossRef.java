package com.mortonsworld.suggest.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"category_id", "id"})
public class FoursquareVenueCategoryCrossRef {
    @NonNull
    public String category_id;

    @NonNull
    public String id;
}
