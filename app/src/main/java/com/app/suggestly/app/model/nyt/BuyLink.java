package com.app.suggestly.app.model.nyt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuyLink {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("url")
    @Expose
    public String url;

}