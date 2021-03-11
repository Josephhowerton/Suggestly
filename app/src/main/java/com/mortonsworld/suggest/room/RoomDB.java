package com.mortonsworld.suggest.room;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mortonsworld.suggest.model.foursquare.FoursquareCategory;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenueCategoryCrossRef;
import com.mortonsworld.suggest.model.nyt.Book;
import com.mortonsworld.suggest.model.user.User;

import org.jetbrains.annotations.NotNull;

@Database(entities = {FoursquareVenue.class, FoursquareCategory.class, User.class, FoursquareVenueCategoryCrossRef.class, Book.class}, exportSchema = false, version = 11)
@TypeConverters({Converter.class})
public abstract class RoomDB extends RoomDatabase {
    public final static String DATABASE_NAME = "RoomDB";
    public static RoomDB INSTANCE;

    public static RoomDB getInstance(Application application){
        if(INSTANCE == null){
            synchronized (RoomDB.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(application, RoomDB.class, DATABASE_NAME)
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

    static final Migration MIGRATION_2_3 = new Migration(5, 4) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {

        }
    };
}
