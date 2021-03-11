package com.mortonsworld.suggest.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenueCategoryCrossRef;
import com.mortonsworld.suggest.utility.DistanceCalculator;

import java.util.List;

@Dao
public abstract class FoursquareDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long create(FoursquareVenue venue);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long create(FoursquareVenueCategoryCrossRef categoryCrossRef);

    @Query("SELECT * FROM FoursquareVenue WHERE isRecommended = 1")
    public abstract DataSource.Factory<Integer, FoursquareVenue>  readRecommendedVenues();

    @Query("SELECT * from FoursquareVenue")
    public abstract List<FoursquareVenue> readVenues();

    @Query("SELECT * from FoursquareVenue WHERE id = :id")
    public abstract FoursquareVenue readVenueById(String id);

    @Query("SELECT * FROM FoursquareVenue ORDER BY ABS(lat -:lat) + ABS(lng -:lng) ASC LIMIT 1")
    public abstract FoursquareVenue readClosestEntry(double lat, double lng);

    @Update
    public abstract int update(FoursquareVenue venue);

    @Query("UPDATE foursquarevenue SET phone=:phone, formattedPhone=:formattedPhone,"
            + "twitter=:twitter, instagram=:instagram, facebook=:facebook, facebookName =:facebookName,"
            + "facebookUsername=:facebookUsername WHERE id=:id")
    public abstract int updateVenueContact(String id, String phone,
            String formattedPhone, String twitter, String instagram,
            String facebook, String facebookName, String facebookUsername
    );

    @Query("UPDATE foursquarevenue SET checkinsCount=:checkinsCount, usersCount=:usersCount,"
            + "tipCount=:tipCount, visitsCount=:visitsCount WHERE id=:id")
    public abstract int updateVenueStats(String id, long checkinsCount, long usersCount,
                                         long tipCount, long visitsCount);

    @Query("UPDATE foursquarevenue SET url=:url, rating=:rating,"
            + "rating_color=:ratingColor, rating_signals=:ratingSignals, description=:description WHERE id=:id")
    public abstract int updateVenueDescription(String id, String url, float rating,
                                               String ratingColor, long ratingSignals,
                                               String description);

    @Query("UPDATE foursquarevenue SET prefix=:prefix, suffix=:suffix,"
            + "width=:width, height=:height, visibility=:visibility WHERE id=:id")
    public abstract int updateVenueImage(String id, String prefix, String suffix,
                                         int width, int height,
                                         String visibility);

    @Query("UPDATE foursquarevenue SET status=:status, isOpen=:isOpen,"
            + "isLocalHoliday=:isLocalHoliday WHERE id=:id")
    public abstract int updateVenueHours(String id, String status,
                                         boolean isOpen, boolean isLocalHoliday);

    @Query("UPDATE foursquarevenue SET isRecommended=:isRecommended WHERE id=:id")
    public abstract int updateVenueRecommended(String id, boolean isRecommended);

    @Query("UPDATE foursquarevenue SET hasDetails=:hasDetails WHERE id=:id")
    public abstract int updateVenueHasDetails(String id, boolean hasDetails);

    @Delete
    public abstract int delete(FoursquareVenue venue);

    @Transaction
    public Boolean isEmpty(FoursquareVenue venue){
        return venue == null;
    }

    @Transaction
    public Boolean isFresh(double lat, double lng) {
        FoursquareVenue venue = readClosestEntry(lat , lng);
        boolean isEmpty = isEmpty(venue);
        if (isEmpty) {
            return false;
        }
        double distance = DistanceCalculator.distanceMile(lat, venue.getLocation().lat, lng, venue.getLocation().lng);

        return distance <= 5.0;
    }
}
