package com.mortonsworld.suggestly.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.utility.DistanceCalculator;

import java.util.List;

@Dao
public abstract class FoursquareDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long create(Venue venue);

    @Query("SELECT * from Venue WHERE id = :id")
    public abstract Venue readVenueById(String id);

    @Transaction
    @Query("SELECT * FROM venue v JOIN category c ON(v.venue_category_id = c.category_id) WHERE is_venue_recommended = 1")
    public abstract DataSource.Factory<Integer, VenueAndCategory> readRecommendedVenuesDataFactory();

    @Transaction
    @Query("SELECT * FROM Venue v JOIN category c ON(v.venue_category_id = c.category_id) WHERE is_venue_recommended = 1")
    public abstract List<VenueAndCategory> readRecommendedVenuesLiveData();

    @Transaction
    @Query("select * from venue v JOIN category c ON(v.venue_category_id = c.category_id) where venue_category_id IN (select child from categoryclosure where parent =:categoryId) ")
    public abstract DataSource.Factory<Integer, VenueAndCategory> readVenueByCategoryId(String categoryId);

    @Transaction
    @Query("select * from venue v JOIN category c ON(v.venue_category_id = c.category_id) where venue_category_id IN (select child from categoryclosure where parent =:categoryId) ")
    public abstract List<VenueAndCategory> readVenueByCategoryIdLiveData(String categoryId);

    @Query("SELECT * FROM Venue ORDER BY ABS(lat -:lat) + ABS(lng -:lng) ASC LIMIT 1")
    public abstract Venue readClosestEntry(double lat, double lng);

    @Update
    public abstract int update(Venue venue);

    @Query("UPDATE Venue SET phone=:phone, formattedPhone=:formattedPhone,"
            + "twitter=:twitter, instagram=:instagram, facebook=:facebook, facebookName =:facebookName,"
            + "facebookUsername=:facebookUsername, venue_updated_at=DATE('now') WHERE id=:id")
    public abstract int updateVenueContact(String id, String phone,
            String formattedPhone, String twitter, String instagram,
            String facebook, String facebookName, String facebookUsername
    );

    @Query("UPDATE Venue SET checkinsCount=:checkinsCount, usersCount=:usersCount,"
            + "tipCount=:tipCount, visitsCount=:visitsCount WHERE id=:id")
    public abstract int updateVenueStats(String id, long checkinsCount, long usersCount,
                                         long tipCount, long visitsCount);

    @Query("UPDATE Venue SET venue_url=:url, venue_rating=:rating,"
            + "venue_rating_color=:ratingColor, venue_rating_signals=:ratingSignals, venue_description=:description, "
            + "venue_updated_at=DATE('now') WHERE id=:id")
    public abstract int updateVenueDescription(String id, String url, float rating,
                                               String ratingColor, long ratingSignals,
                                               String description);

    @Query("UPDATE Venue SET venue_photo_prefix=:prefix, venue_photo_suffix=:suffix,"
            + "venue_photo_width=:width, venue_photo_height=:height, venue_photo_visibility=:visibility,"
            + "venue_updated_at=DATE('now') WHERE id=:id")
    public abstract int updateVenueImage(String id, String prefix, String suffix,
                                         int width, int height,
                                         String visibility);

    @Query("UPDATE Venue SET status=:status, isOpen=:isOpen,"
            + "isLocalHoliday=:isLocalHoliday, venue_updated_at=DATE('now')  WHERE id=:id")
    public abstract int updateVenueHours(String id, String status,
                                         boolean isOpen, boolean isLocalHoliday);

    @Query("UPDATE Venue SET is_venue_recommended=:isRecommended, venue_updated_at=DATE('now')  WHERE id=:id")
    public abstract int updateVenueRecommended(String id, boolean isRecommended);

    @Query("UPDATE Venue SET venue_has_details=:hasDetails, venue_updated_at=DATE('now')  WHERE id=:id")
    public abstract int updateVenueHasDetails(String id, boolean hasDetails);

    @Delete
    public abstract int delete(Venue venue);

    @Transaction
    public Boolean isEmpty(Venue venue){
        return venue == null;
    }

    @Transaction
    public Boolean isFresh(double lat, double lng) {
        Venue venue = readClosestEntry(lat , lng);
        boolean isEmpty = isEmpty(venue);
        if (isEmpty) {
            return false;
        }
        double distance = DistanceCalculator.distanceMile(lat, venue.getLocation().lat, lng, venue.getLocation().lng);

        return distance <= 5.0;
    }
}
