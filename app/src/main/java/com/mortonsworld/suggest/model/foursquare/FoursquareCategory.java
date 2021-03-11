package com.mortonsworld.suggest.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@Entity()
public class FoursquareCategory {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_key")
    @NonNull
    private Integer primaryKey;

    @ColumnInfo(name = "child_id")
    private String childId;

    private Integer depth;

    @SerializedName("id")
    @ColumnInfo(name = "category_id")
    @NonNull
    public String categoryId;

    @SerializedName("name")
    public String name;

    @SerializedName("pluralName")
    @ColumnInfo(name = "plural_name")
    public String pluralName;

    @SerializedName("shortName")
    @ColumnInfo(name = "short_name")
    public String shortName;

    @SerializedName("icon")
    @Embedded public Icon icon;

    @Ignore public List<FoursquareCategory> categories;

    public static class Icon{
        public String prefix;
        public String suffix;
    }

    public FoursquareCategory(){}

    @NonNull
    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(@NonNull Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean hasChildren(){
        return categories != null && categories.size() > 0;
    }

    public FoursquareCategory removeChild(){
        if(categories != null && hasChildren()){
            return categories.remove(0);
        }
        return null;
    }
}
