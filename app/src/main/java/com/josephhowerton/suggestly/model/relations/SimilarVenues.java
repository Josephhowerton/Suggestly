package com.josephhowerton.suggestly.model.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"ownerId", "siblingId"})
public class SimilarVenues {
    @NonNull
    public String ownerId;
    @NonNull
    public String siblingId;
}
