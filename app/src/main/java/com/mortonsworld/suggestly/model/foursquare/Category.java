package com.mortonsworld.suggestly.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.utility.SuggestionType;

import java.util.List;

@Entity()
public class Category extends Suggestion {
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    @NonNull
    public String id;

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

    @Ignore public List<Category> categories;

    public static class Icon{
        public String prefix;
        public String suffix;
    }

    public Category(){}

    public String getId() {
        return id;
    }

    public void setId(String categoryId) {
        this.id = categoryId;
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

    public Category removeChild(){
        if(categories != null && hasChildren()){
            return categories.remove(0);
        }
        return null;
    }

    public SuggestionType getSuggestionType(){
        return SuggestionType.FOURSQUARE_CATEGORY;
    }
}
