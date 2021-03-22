package com.mortonsworld.suggestly.source;

import android.app.Application;

import androidx.room.Insert;

import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;
import com.mortonsworld.suggestly.room.RoomDB;
import com.mortonsworld.suggestly.room.UserDao;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserSource {
    private final UserDao userDao;
    private final ExecutorService executorService;
    public UserSource(Application application, ExecutorService service){
        userDao = RoomDB.getInstance(application).getUserDao();
        executorService = service;
    }

    public void checkIfUserInRoom(String id, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            Boolean isFresh = userDao.checkIfUserExist(id);

            if(isFresh){
                source.onNext(isFresh);
            }else{
                source.onNext(isFresh);
            }

            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    @Insert
    public void createUser(User user, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            long result = userDao.createUser(user);
            if(result >= 0){
                source.onNext(true);
            }else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void readUser(String id, Observer<User> observer){
        userDao.readUser(id).subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void readUserLocation(String id, Observer<LocationTuple> observer){
        userDao.readUserLocationObservable(id).subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public LocationTuple readUserLocation(String id){
        try {
            executorService.submit(() -> userDao.readUserLocation(id)).get();
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        return null;
    }

    public void updateUserLastSignedIn(String id, Date latestSignIn, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            int result = userDao.updateUserLastSignedIn(id, latestSignIn);
            if(result >= 0){
                source.onNext(true);
            }else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void updateUserLocation(String id, double lat, double lng, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            int result = userDao.updateUserLocation(id, lat, lng);
            if(result >= 0){
                source.onNext(true);
            }else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void updateUser(User user, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            int result = userDao.updateUser(user);
            if(result >= 0){
                source.onNext(true);
            }else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void updateUserSavedSuggestions(String id, List<Suggestion> suggestions, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void updateUserFavoriteSuggestions(String id, List<Suggestion> suggestions, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void deleteUser(Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            User user = userDao.readCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
            int result = userDao.deleteUser(user);
            if(result >= 0) {
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(onSuccess -> {
                    source.onNext(true);
                    source.onComplete();
                }).addOnFailureListener(onFailure -> {
                    source.onNext(false);
                    source.onComplete();
                });
            }else{
                source.onNext(false);
                source.onComplete();
            }
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }
}
