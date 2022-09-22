package com.app.suggestly.app.model.nyt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookResponse {
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("copyright")
    @Expose
    public String copyright;
    @SerializedName("num_results")
    @Expose
    public Integer numResults;
    @SerializedName("last_modified")
    @Expose
    public String lastModified;
    @SerializedName("results")
    @Expose
    public Result results;
}