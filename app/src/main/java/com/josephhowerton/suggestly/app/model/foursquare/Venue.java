package com.josephhowerton.suggestly.app.model.foursquare;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.josephhowerton.suggestly.app.model.Suggestion;
import com.josephhowerton.suggestly.utility.SuggestionType;

import java.sql.Date;
import java.util.List;

@Entity
public class Venue extends Suggestion {

    @ColumnInfo(name = "id")
    @SerializedName(value = "id")
    @PrimaryKey
    @NonNull
    public String venueId;

    @NonNull
    @ColumnInfo(name = "venue_category_id")
    public String categoryId;

    @ColumnInfo(name = "venue_name")
    public String name;
    
    @Embedded public Contact contact;
    @Embedded public Location location;
    @Ignore public List<Category> categories;

    @ColumnInfo(name = "venue_verified")
    public boolean verified;
    @Embedded public Stats stats;

    @ColumnInfo(name = "venue_url")
    public String url;
    
    @ColumnInfo(name = "venue_rating")
    public Float rating;
    
    @ColumnInfo(name = "venue_rating_color")
    public String ratingColor;
    
    @ColumnInfo(name = "venue_rating_signals")
    public Long ratingSignals;
    
    @ColumnInfo(name = "venue_description")
    public String description;
    
    @Embedded public Hours hours;
    @Embedded public BestPhoto bestPhoto;
    
    @ColumnInfo(name = "is_venue_recommended")
    public Boolean isRecommended = false;

    @Ignore
    public Boolean isSaved = false;

    @ColumnInfo(name = "venue_has_details")
    public Boolean hasDetails = false;

    @ColumnInfo(name = "venue_created_at", defaultValue = "CURRENT_TIMESTAMP")
    public Date venueCreatedDate;

    @ColumnInfo(name = "venue_updated_at", defaultValue = "CURRENT_TIMESTAMP")
    public Date venueUpdatedDate;

    @ColumnInfo(name = "venue_icon_prefix")
    public String prefix;

    @ColumnInfo(name = "venue_icon_suffix")
    public String suffix;


    public Venue() {
        venueId = "venueId";
        categoryId = "categoryId";
    }

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
        @ColumnInfo(name = "venue_photo_id")
        public String id;
        @ColumnInfo(name = "venue_photo_prefix")
        public String prefix;
        @ColumnInfo(name = "venue_photo_suffix")
        public String suffix;
        @ColumnInfo(name = "venue_photo_width")
        public int width;
        @ColumnInfo(name = "venue_photo_height")
        public int height;
        @ColumnInfo(name = "venue_photo_visibility")
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

    public String getFormattedAddress(){
        String formattedAddress = "";
        if(location != null){
            if(location.address != null){
                formattedAddress += location.address;
            }

            if(location.city != null){
                formattedAddress += "\n" + location.city;
            }

            if(location.country != null){
                formattedAddress += " " + location.country;
            }

            if(location.postalCode != null){
                formattedAddress += " " + location.postalCode;
            }
        }
        return formattedAddress;
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

    public String getBestPhotoUrl(){
        if(bestPhoto != null){
            return bestPhoto.prefix + bestPhoto.width + "x" + bestPhoto.height + bestPhoto.suffix;
        }
        return "";
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

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()){
            return false;
        }else{
            Venue venue = (Venue) obj;
            if(compareDistance(venue.location.lat, venue.location.lng)){
                return venueId.equals(venue.venueId);
            }else{
               return false;
            }
        }
    }

    public boolean isBestPhotoSame(Venue oldVenue){
        if(bestPhoto != null && oldVenue.bestPhoto != null){
            return !bestPhoto.id.equals(oldVenue.bestPhoto.id);
        }
        return false;
    }

    public boolean compareDistance(double lat, double lng){
        double epsilon = 0.000001d;
        return (Math.abs(location.lat - lat) < epsilon && Math.abs(location.lng - lng) < epsilon);
    }
}
