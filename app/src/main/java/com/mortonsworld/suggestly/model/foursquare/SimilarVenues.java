package com.mortonsworld.suggestly.model.foursquare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SimilarVenues {
    @PrimaryKey(autoGenerate = true)
    public int pk;
    public String ownerId;
    public String siblingId;
}
