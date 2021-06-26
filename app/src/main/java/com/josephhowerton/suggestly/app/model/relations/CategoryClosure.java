package com.josephhowerton.suggestly.app.model.relations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.jetbrains.annotations.NotNull;

@Entity(primaryKeys = {"parent", "child"})
public class CategoryClosure {
    @NonNull
    public String parent;

    @NonNull
    public String child;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    public int depth;

    public CategoryClosure(@NotNull String parent, @NotNull String child, @NotNull int depth) {
        this.parent = parent;
        this.child = child;
        this.depth = depth;
    }
}
