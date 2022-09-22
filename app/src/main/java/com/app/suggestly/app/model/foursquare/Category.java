package com.app.suggestly.app.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.app.suggestly.app.model.Suggestion;
import com.app.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity()
public class Category extends Suggestion {
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    @NonNull
    public String id;

    @SerializedName("name")
    @ColumnInfo(name = "category_name")
    public String name;

    @SerializedName("pluralName")
    @ColumnInfo(name = "category_plural_name")
    public String pluralName;

    @SerializedName("shortName")
    @ColumnInfo(name = "category_short_name")
    public String shortName;

    @SerializedName("icon")
    @Embedded public Icon icon;

    @Ignore public List<Category> categories;

    public static class Icon{
        @ColumnInfo(name = "category_icon_prefix")
        public String prefix;

        @ColumnInfo(name = "category_icon_suffix")
        public String suffix;
    }

    public Category(){}

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String categoryId) {
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

    public String getIconWithBGUrl(int size){
        if(icon != null){
            return icon.prefix + "bg_" + size + icon.suffix;
        }
        return "";
    }

    public String getIconUrl(int size){
        if(icon != null){
            return icon.prefix + size + icon.suffix;
        }
        return "";
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

    @NotNull
    public SuggestionType getSuggestionType(){
        return SuggestionType.FOURSQUARE_CATEGORY;
    }
}
