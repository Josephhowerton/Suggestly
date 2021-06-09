package com.josephhowerton.suggestly.source;

import android.app.Application;

import androidx.room.Insert;

import com.google.firebase.auth.FirebaseAuth;
import com.josephhowerton.suggestly.model.Suggestion;
import com.josephhowerton.suggestly.model.foursquare.Venue;
import com.josephhowerton.suggestly.model.nyt.Book;
import com.josephhowerton.suggestly.model.relations.VenueAndCategory;
import com.josephhowerton.suggestly.model.user.LocationTuple;
import com.josephhowerton.suggestly.model.user.User;
import com.josephhowerton.suggestly.room.RoomDB;
import com.josephhowerton.suggestly.room.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserSource {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserSource(Application application){
        userDao = RoomDB.getInstance(application).getUserDao();
        executorService = Executors.newFixedThreadPool(5);
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

    public void deleteUser(Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                source.onNext(false);
            }

            User user = userDao.readCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
            int result = userDao.deleteUser(user);
            if(result >= 0) {
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(onSuccess -> {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        FirebaseAuth.getInstance().signOut();
                    }
                    source.onNext(true);
                    source.onComplete();
                }).addOnFailureListener(onFailure -> {
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        FirebaseAuth.getInstance().signOut();
                    }

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

    public void readSavedSuggestions(Observer<List<Suggestion>> observer){
        Observable<List<Suggestion>> observable = Observable.create(source -> {
            List<Suggestion> suggestions = new ArrayList<>(readSavedVenueSuggestion());
            suggestions.addAll(readSavedBooksSuggestion());
            source.onNext(suggestions);
            source.onComplete();
        });


        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void readFavoriteSuggestions(Observer<List<Suggestion>> observer){
        Observable<List<Suggestion>> observable = Observable.create(source -> {
            List<Suggestion> suggestions = new ArrayList<>(readFavoriteVenueSuggestion());
            suggestions.addAll(readFavoriteBooksSuggestion());
            source.onNext(suggestions);
            source.onComplete();
        });


        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public long upsertSavedVenue(Venue venue, boolean isSaved){
        if(FirebaseAuth.getInstance().getCurrentUser() == null || venue ==null){
            return -1;
        }
        try{
            return executorService.submit(() -> userDao.upsertSavedVenue(FirebaseAuth.getInstance().getCurrentUser().getUid(), venue.venueId, isSaved)).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    private List<VenueAndCategory> readSavedVenueSuggestion(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            return userDao.readSavedVenues(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return new ArrayList<>();
    }

    private List<VenueAndCategory> readFavoriteVenueSuggestion(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            return userDao.readFavoriteVenues(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return new ArrayList<>();
    }

    public List<VenueAndCategory> readSavedVenues(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try{
                return executorService.submit(() -> userDao.readSavedVenues(FirebaseAuth.getInstance().getCurrentUser().getUid())).get();
            }catch (InterruptedException|ExecutionException e){
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public List<VenueAndCategory> readFavoriteVenues(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try{
                return executorService.submit(() -> userDao.readFavoriteVenues(FirebaseAuth.getInstance().getCurrentUser().getUid())).get();
            }catch (InterruptedException|ExecutionException e){
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public long upsertFavoriteVenue(Venue venue, boolean isFavorite){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return -1;
        }

        try{
            return executorService.submit(() -> userDao.upsertFavoriteVenue(FirebaseAuth.getInstance().getCurrentUser().getUid(), venue.venueId, isFavorite)).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    public int deletedSavedVenue(Venue venue){
        if(FirebaseAuth.getInstance().getCurrentUser() == null || venue == null){
            return -1;
        }
        try{
            return executorService.submit(() -> userDao.deletedSavedVenue(FirebaseAuth.getInstance().getCurrentUser().getUid(), venue.venueId)).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    private List<Book> readSavedBooksSuggestion(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return userDao.readSavedBook(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return new ArrayList<>();
    }

    public List<Book> readSavedBooks(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                return executorService.submit(() -> userDao.readSavedBook(FirebaseAuth.getInstance().getCurrentUser().getUid())).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public long upsertSavedBook(Book book, boolean isSaved){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return -1;
        }

        try{
            return executorService.submit(() -> userDao.upsertSavedBook(FirebaseAuth.getInstance().getCurrentUser().getUid(), book.getPrimaryIsbn13(), isSaved)).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    public List<Book> readFavoriteBooks(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                return executorService.submit(() -> userDao.readFavoriteBook(FirebaseAuth.getInstance().getCurrentUser().getUid())).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public List<Book> readFavoriteBooksSuggestion(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            return userDao.readFavoriteBook(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return new ArrayList<>();
    }

    public long upsertFavoriteBook(Book book, boolean isFavorite){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return -1;
        }

        try{
            return executorService.submit(() -> userDao.upsertFavoriteBook(FirebaseAuth.getInstance().getCurrentUser().getUid(), book.getPrimaryIsbn13(), isFavorite)).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }

    public int deletedSavedBook(Book book){
        if(FirebaseAuth.getInstance().getCurrentUser() == null || book == null){
            return -1;
        }
        try{
            return executorService.submit(() -> userDao.deletedSavedBook(FirebaseAuth.getInstance().getCurrentUser().getUid(), book.getPrimaryIsbn13())).get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        return -1;
    }
}
