package com.mortonsworld.suggestly.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.utility.SuggestionType;

import java.sql.Date;
import java.util.List;

@Entity
public class Venue extends Suggestion {

    @ColumnInfo(name = "id")
    @PrimaryKey
    @NonNull
    public String venueId;

    @ColumnInfo(name = "category_id")
    public String categoryId;
    public String name;
    @Embedded public Contact contact;
    @Embedded public Location location;
    @Ignore public List<Category> categories;
    public boolean verified;
    @Embedded public Stats stats;
    public String url;
    public Float rating;
    @ColumnInfo(name = "rating_color")
    public String ratingColor;
    @ColumnInfo(name = "rating_signals")
    public Long ratingSignals;
    public String description;
    @Embedded public Hours hours;
    @Embedded public BestPhoto bestPhoto;
    public Boolean isRecommended = false;
    public Boolean hasDetails = false;

    public Date createdAt;
    public Date updateAt;

    public Venue() {}

    public static class Stats{
        public long checkinsCount;
        public long usersCount;
        public long tipCount;
        public long visitsCount;

        public long getCheckinsCount() {
            return checkinsCount;
        }

        public void setCheckinsCount(long checkinsCount) {
            this.checkinsCount = checkinsCount;
        }

        public long getUsersCount() {
            return usersCount;
        }

        public void setUsersCount(long usersCount) {
            this.usersCount = usersCount;
        }

        public long getTipCount() {
            return tipCount;
        }

        public void setTipCount(long tipCount) {
            this.tipCount = tipCount;
        }

        public long getVisitsCount() {
            return visitsCount;
        }

        public void setVisitsCount(long visitsCount) {
            this.visitsCount = visitsCount;
        }
    }

    public static class Link{
        public Long count;
        public List<Item> items;

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    public static class Item{
        public String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Hours{
        public String status;
        public boolean isOpen;
        public boolean isLocalHoliday;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public boolean isLocalHoliday() {
            return isLocalHoliday;
        }

        public void setLocalHoliday(boolean localHoliday) {
            isLocalHoliday = localHoliday;
        }
    }

    public static class BestPhoto{
        @ColumnInfo(name = "photo_id")
        public String id;
        public String prefix;
        public String suffix;
        public int width;
        public int height;
        public String visibility;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getVisibility() {
            return visibility;
        }

        public void setVisibility(String visibility) {
            this.visibility = visibility;
        }
    }

    public void addVenueDetails(Venue venue){
        if(venue.contact != null){
            this.contact.phone = venue.contact.phone;
            this.contact.formattedPhone = venue.contact.formattedPhone;
            this.contact.twitter = venue.contact.twitter;
            this.contact.instagram = venue.contact.instagram;
            this.contact.facebook = venue.contact.facebook;
            this.contact.facebookUsername = venue.contact.facebookUsername;
            this.contact.facebookName = venue.contact.facebookName;
        }

        if(venue.stats != null){
            this.stats.checkinsCount = venue.stats.checkinsCount;
            this.stats.usersCount = venue.stats.usersCount;
            this.stats.tipCount = venue.stats.tipCount;
            this.stats.visitsCount = venue.stats.visitsCount;
        }

        this.url = venue.url;
        this.rating = venue.rating;
        this.ratingColor = venue.ratingColor;
        this.ratingSignals = venue.ratingSignals;
        this.description = venue.description;

        if(venue.hours != null){
            this.hours.status = venue.hours.status;
            this.hours.isOpen = venue.hours.isOpen;
            this.hours.isLocalHoliday = venue.hours.isLocalHoliday;
        }

        if(venue.bestPhoto != null){
            this.bestPhoto.prefix = venue.bestPhoto.prefix;
            this.bestPhoto.suffix = venue.bestPhoto.suffix;
            this.bestPhoto.width = venue.bestPhoto.width;
            this.bestPhoto.height = venue.bestPhoto.height;
            this.bestPhoto.visibility = venue.bestPhoto.visibility;
        }
        verified = venue.verified;
    }

    @NonNull
    public String getId() {
        return venueId;
    }

    @NonNull
    @Override
    public SuggestionType getSuggestionType() {
        return SuggestionType.FOURSQUARE_VENUE;
    }

    public void setId(@NonNull String id) {
        this.venueId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getRatingColor() {
        return ratingColor;
    }

    public void setRatingColor(String ratingColor) {
        this.ratingColor = ratingColor;
    }

    public Long getRatingSignals() {
        return ratingSignals;
    }

    public void setRatingSignals(Long ratingSignals) {
        this.ratingSignals = ratingSignals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Hours getHours() {
        return hours;
    }

    public void setHours(Hours hours) {
        this.hours = hours;
    }

    public BestPhoto getBestPhoto() {
        return bestPhoto;
    }

    public void setBestPhoto(BestPhoto bestPhoto) {
        this.bestPhoto = bestPhoto;
    }

    public Boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(Boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public Boolean getHasDetails() {
        return hasDetails;
    }

    public void setHasDetails(Boolean hasDetails) {
        this.hasDetails = hasDetails;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}
