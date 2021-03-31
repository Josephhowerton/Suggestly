package com.mortonsworld.suggestly.model.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"ownerId", "siblingId"})
public class SimilarVenues {
    @NonNull
    public String ownerId;
    @NonNull
    public String siblingId;
}
