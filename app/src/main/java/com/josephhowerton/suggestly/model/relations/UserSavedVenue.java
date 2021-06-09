package com.josephhowerton.suggestly.model.relations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"uid", "vid"})
public class UserSavedVenue {
    @NonNull
    public String uid;
    @NonNull
    public String vid;

    @ColumnInfo(name = "is_saved", defaultValue = "false")
    public boolean isSaved;

    @ColumnInfo(name = "is_favorite", defaultValue = "false")
    public boolean isFavorite;

    public UserSavedVenue(@NonNull String uid, @NonNull  String vid, @NonNull  Boolean isSaved, @NonNull  Boolean isFavorite){
        this.uid = uid;
        this.vid = vid;
        this.isSaved = isSaved;
        this.isFavorite = isFavorite;
    }
}
