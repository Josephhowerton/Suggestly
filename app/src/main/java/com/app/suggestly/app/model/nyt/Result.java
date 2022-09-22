package com.app.suggestly.app.model.nyt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("list_name")
    @Expose
    public String listName;
    @SerializedName("list_name_encoded")
    @Expose
    public String listNameEncoded;
    @SerializedName("bestsellers_date")
    @Expose
    public String bestsellersDate;
    @SerializedName("published_date")
    @Expose
    public String publishedDate;
    @SerializedName("published_date_description")
    @Expose
    public String publishedDateDescription;
    @SerializedName("next_published_date")
    @Expose
    public String nextPublishedDate;
    @SerializedName("previous_published_date")
    @Expose
    public String previousPublishedDate;
    @SerializedName("display_name")
    @Expose
    public String displayName;
    @SerializedName("normal_list_ends_at")
    @Expose
    public Integer normalListEndsAt;
    @SerializedName("updated")
    @Expose
    public String updated;
    @SerializedName("books")
    @Expose
    public List<Book> books = null;
    @SerializedName("corrections")
    @Expose
    public List<Object> corrections = null;

}