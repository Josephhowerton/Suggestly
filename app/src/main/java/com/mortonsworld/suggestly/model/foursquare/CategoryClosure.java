package com.mortonsworld.suggestly.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity()
public class CategoryClosure {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Integer primaryKey;

    @NonNull
    public String parent;

    @NonNull
    public String child;

    @ColumnInfo(defaultValue = "0")
    public int depth;

    public CategoryClosure(@NotNull String parent, @NotNull String child, @NotNull int depth) {
        this.parent = parent;
        this.child = child;
        this.depth = depth;
    }
}
