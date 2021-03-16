package com.mortonsworld.suggestly.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;

import java.sql.Date;

import io.reactivex.rxjava3.core.Observable;

@Dao
public abstract class UserDao {
    @Transaction
    public Boolean checkIfUserExist(String id){
        return doesUserExist(id) != null;
    }

    @Query("select * from user where id=:id")
    public abstract User doesUserExist(String id);

    @Insert
    public abstract long createUser(User user);

    @Query("select * from user where id=:id")
    public abstract Observable<User> readUser(String id);

    @Query("select * from user where id=:id")
    public abstract User readCurrentUser(String id);

    @Query("select lat,lng from user where id=:id")
    public abstract Observable<LocationTuple> readUserLocation(String id);

    @Query("UPDATE user SET latest_sign_in =:latestSignIn WHERE id=:id")
    public abstract int updateUserLastSignedIn(String id, Date latestSignIn);

    @Query("UPDATE user SET lat =:lat, lng =:lng WHERE id=:id")
    public abstract int updateUserLocation(String id, double lat, double lng);

    @Update
    public abstract int updateUser(User user);

    @Delete
    public abstract int deleteUser(User user);

}
