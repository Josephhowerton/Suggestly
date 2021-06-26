package com.josephhowerton.suggestly.app.model.nyt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ISBN {
    @SerializedName("isbn10")
    @Expose
    public String isbn10;

    @SerializedName("isbn13")
    @Expose
    public String isbn13;


}