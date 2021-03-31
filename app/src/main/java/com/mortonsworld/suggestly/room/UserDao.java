package com.mortonsworld.suggestly.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.UserSavedBook;
import com.mortonsworld.suggestly.model.relations.UserSavedVenue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;

import java.sql.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

@Dao
public abstract class UserDao {
    @Transaction
    public Boolean checkIfUserExist(String id){
        return doesUserExist(id) != null;
    }

    @Query("SELECT * FROM user WHERE id=:id")
    public abstract User doesUserExist(String id);

    @Insert
    public abstract long createUser(User user);

    @Query("SELECT * FROM user WHERE id=:id")
    public abstract Observable<User> readUser(String id);

    @Query("SELECT * FROM user WHERE id=:id")
    public abstract User readCurrentUser(String id);

    @Query("SELECT lat,lng FROM user WHERE id=:id")
    public abstract Observable<LocationTuple> readUserLocationObservable(String id);

    @Query("UPDATE user SET lat =:lat, lng =:lng WHERE id=:id")
    public abstract int updateUserLocation(String id, double lat, double lng);

    @Delete
    public abstract int deleteUser(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveVenue(UserSavedVenue savedVenues);

    @Query("SELECT * FROM UserSavedVenue WHERE uid=:uid AND vid=:vid")
    public abstract UserSavedVenue readSavedVenues(String uid, String vid);

    @Query("SELECT * FROM Venue v JOIN category c ON(v.venue_category_id = c.category_id) WHERE id IN (SELECT vid FROM UserSavedVenue WHERE is_saved=1 AND uid=:uid)")
    public abstract List<VenueAndCategory> readSavedVenues(String uid);

    @Query("UPDATE UserSavedVenue SET is_saved=:saved WHERE uid=:uid AND vid=:vid ")
    public abstract int updateSavedVenue(String uid, String vid, boolean saved);

    @Transaction
    public long upsertSavedVenue(String uid, String vid, boolean isSaved){
        UserSavedVenue userSavedVenue = new UserSavedVenue(uid, vid, isSaved, false);
        long result = saveVenue(userSavedVenue);
        if(result < 0){
            result = updateSavedVenue(uid, vid, isSaved);
            deletedUnsavedVenue();
        }
        return result;
    }

    @Query("SELECT * FROM Venue v JOIN category c ON(v.venue_category_id = c.category_id )WHERE id IN (SELECT vid FROM UserSavedVenue WHERE is_favorite=1 AND uid=:uid)")
    public abstract List<VenueAndCategory> readFavoriteVenues(String uid);

    @Query("UPDATE UserSavedVenue SET is_favorite=:favorite WHERE uid=:uid AND vid=:vid ")
    public abstract int updateFavoriteVenue(String uid, String vid, boolean favorite);

    @Transaction
    public long upsertFavoriteVenue(String uid, String vid, boolean isFavorite){
        UserSavedVenue userSavedVenue = new UserSavedVenue(uid, vid, false, isFavorite);
        long result = saveVenue(userSavedVenue);
        if(result < 0){
            result = updateFavoriteVenue(uid, vid, isFavorite);
            deletedUnsavedVenue();
        }
        return result;
    }

    @Transaction
    public int deletedSavedVenue(String uid, String vid){
        UserSavedVenue savedVenue = readSavedVenues(uid, vid);
        return deletedSavedVenue(savedVenue);
    }

    @Delete
    public abstract int deletedSavedVenue(UserSavedVenue savedVenues);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long saveBook(UserSavedBook savedBook);

    @Query("SELECT * FROM UserSavedBook WHERE uid=:uid AND bid=:bid")
    public abstract UserSavedBook readSavedBook(String uid, String bid);

    @Query("SELECT * FROM Book WHERE primaryIsbn13 IN (SELECT bid FROM UserSavedBook WHERE is_saved=1 AND uid=:uid)")
    public abstract List<Book> readSavedBook(String uid);

    @Query("UPDATE UserSavedBook SET is_saved=:favorite WHERE uid=:uid AND bid=:bid ")
    public abstract int updateSavedBook(String uid, String bid, boolean favorite);

    @Transaction
    public long upsertSavedBook(String uid, String bid, boolean isSaved){
        UserSavedBook savedBook = new UserSavedBook(uid, bid, isSaved, false);
        long result = saveBook(savedBook);
        if(result < 0){
            result = updateSavedBook(uid, bid, isSaved);
            deletedUnsavedBook();
        }
        return result;
    }

    @Query("SELECT * FROM Book WHERE primaryIsbn13 IN (SELECT bid FROM UserSavedBook WHERE is_favorite=1 AND uid=:uid)")
    public abstract List<Book> readFavoriteBook(String uid);

    @Query("UPDATE UserSavedBook SET is_saved=:favorite WHERE uid=:uid AND bid=:bid ")
    public abstract int updateFavoriteBook(String uid, String bid, boolean favorite);

    @Transaction
    public long upsertFavoriteBook(String uid, String bid, boolean isFavorite){
        UserSavedBook savedBook = new UserSavedBook(uid, bid, false, isFavorite);
        long result = saveBook(savedBook);
        if(result < 0){
            result = updateFavoriteBook(uid, bid, isFavorite);
            deletedUnsavedBook();
        }
        return result;
    }

    @Transaction
    public int deletedSavedBook(String uid, String bid){
        UserSavedBook savedBook = readSavedBook(uid, bid);
        return deletedSavedBook(savedBook);
    }

    @Delete
    public abstract int deletedSavedBook(UserSavedBook book);

    @Query("DELETE FROM UserSavedBook WHERE is_saved =0 AND is_favorite=0")
    public abstract int deletedUnsavedBook();

    @Query("DELETE FROM UserSavedVenue WHERE is_saved =0 AND is_favorite=0")
    public abstract int deletedUnsavedVenue();
}
