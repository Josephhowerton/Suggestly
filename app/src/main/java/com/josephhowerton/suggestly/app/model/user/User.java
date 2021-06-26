package com.josephhowerton.suggestly.app.model.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.josephhowerton.suggestly.app.model.nyt.Book;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Entity
public class User {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "display_name")
    private String displayName;
    private double lat = 181.0;
    private double lng = 181.0;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "latest_sign_in")
    private Date latestSignIn;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "saved_suggestions")
    private List<Book> savedSuggestions = new ArrayList<>();

    @ColumnInfo(name = "favorite_suggestions")
    private List<Book> favoriteSuggestions = new ArrayList<>();

    public User(String id){
        this.id = id;
    }

    public User(FirebaseUser user){
        this.id = user.getUid();
        this.displayName = user.getDisplayName();
        if(user.getMetadata() != null){
            this.createdAt = new Date(user.getMetadata().getCreationTimestamp());
            this.updatedAt = new Date(user.getMetadata().getCreationTimestamp());
            this.latestSignIn = new Date(user.getMetadata().getLastSignInTimestamp());
        }
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLatestSignIn() {
        return latestSignIn;
    }

    public void setLatestSignIn(Date latestSignIn) {
        this.latestSignIn = latestSignIn;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Book> getSavedSuggestions() {
        return savedSuggestions;
    }

    public void setSavedSuggestions(List<Book> savedSuggestions) {
        this.savedSuggestions = savedSuggestions;
    }

    public void addSavedSuggestion(Book suggestion) {
        this.savedSuggestions.add(suggestion);
    }

    public boolean isSavedSuggestionsEmpty(){
        return savedSuggestions.isEmpty();
    }

    public List<Book> getFavoriteSuggestions() {
        return favoriteSuggestions;
    }

    public void setFavoriteSuggestions(List<Book> favoriteSuggestions) {
        this.favoriteSuggestions = favoriteSuggestions;
    }

    public void addFavoriteSuggestion(Book suggestion) {
        this.favoriteSuggestions.add(suggestion);
    }

    public boolean isFavoritesSuggestionsEmpty(){
        return savedSuggestions.isEmpty();
    }
}
