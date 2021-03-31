package com.mortonsworld.suggestly.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.CategoryClosure;
import com.mortonsworld.suggestly.model.relations.SimilarVenues;
import com.mortonsworld.suggestly.model.relations.UserSavedBook;
import com.mortonsworld.suggestly.model.relations.UserSavedVenue;
import com.mortonsworld.suggestly.model.user.User;

@Database(entities = {Venue.class, SimilarVenues.class, Category.class,
        CategoryClosure.class, User.class, Book.class,
        UserSavedBook.class, UserSavedVenue.class}, exportSchema = false, version = 41)
@TypeConverters({Converter.class})
public abstract class RoomDB extends RoomDatabase {
    public final static String DATABASE_NAME = "RoomDB";
    public static RoomDB INSTANCE;

    public static RoomDB getInstance(Context context){
        if(INSTANCE == null){
            synchronized (RoomDB.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, RoomDB.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract FoursquareDao getFoursquareDao();
    public abstract UserDao getUserDao();
    public abstract FoursquareCategoryDao getFoursquareCategoryDao();
    public abstract NewYorkTimesDAO getNewYorkTimesDAO();
    public abstract SearchDao getSearchDAO();
}
