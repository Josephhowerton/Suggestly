package com.mortonsworld.suggestly.model.relations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"uid", "bid"})
public class UserSavedBook {
    @NonNull
    public String uid;
    @NonNull
    public String bid;

    @ColumnInfo(name = "is_saved", defaultValue = "false")
    public boolean isSaved;

    @ColumnInfo(name = "is_favorite", defaultValue = "false")
    public boolean isFavorite;

    public UserSavedBook(@NonNull String uid, @NonNull String bid, @NonNull Boolean isSaved, @NonNull Boolean isFavorite){
        this.uid = uid;
        this.bid = bid;
        this.isSaved = isSaved;
        this.isFavorite = isFavorite;
    }
}
