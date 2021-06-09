package com.josephhowerton.suggestly.model.nyt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListName {
    @SerializedName("list_id")
    @Expose
    public Integer listId;

    @SerializedName("list_name")
    @Expose
    public String listName;

    @SerializedName("display_name")
    @Expose
    public String displayName;

    @SerializedName("updated")
    @Expose
    public String updated;

    @SerializedName("list_image")
    @Expose
    public String listImage;

    @SerializedName("books")
    @Expose
    public List<Book> books = null;
}