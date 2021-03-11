package com.mortonsworld.suggest.source;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.mortonsworld.suggest.model.foursquare.CategoryWithVenues;
import com.mortonsworld.suggest.model.foursquare.FoursquareCategory;
import com.mortonsworld.suggest.model.foursquare.FoursquareResult;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenueCategoryCrossRef;
import com.mortonsworld.suggest.room.FoursquareCategoryDao;
import com.mortonsworld.suggest.utility.Config;
import com.mortonsworld.suggest.retrofit.FoursquareManager;
import com.mortonsworld.suggest.retrofit.FoursquareService;
import com.mortonsworld.suggest.retrofit.ServiceFactory;
import com.mortonsworld.suggest.room.FoursquareDao;
import com.mortonsworld.suggest.room.RoomDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FoursquareSource {
    private final FoursquareDao foursquareDao;
    private final FoursquareCategoryDao foursquareCategoryDao;
    private final FoursquareService foursquareService;
    private final ExecutorService executorService;
    public FoursquareSource(Application application, ExecutorService service) {
        this.foursquareDao = RoomDB.getInstance(application).getFoursquareDao();
        this.foursquareCategoryDao = RoomDB.getInstance(application).getFoursquareCategoryDao();
        this.foursquareService = ServiceFactory.getFoursquareClient(Config.FOURSQUARE_BASE_URL, FoursquareService.class);
        executorService = service;
    }

/* *****************************************************************************************
    FOURSQUARE CATEGORIES
******************************************************************************************** */
    public void isCategoryTableEmpty(Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            Boolean isEmpty = foursquareCategoryDao.isTableEmpty();
            if(isEmpty){
                source.onNext(true);
            }else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void getCategories(Observer<List<FoursquareCategory>> observer){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getFoursquareCategories(searchParameters)
                .map(response -> response.response.categories)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void buildCategoryClosureTable(FoursquareCategory target, List<FoursquareCategory> tree){
        tree.add(target);
        while (target.hasChildren()){
            FoursquareCategory child = target.removeChild();
            child.setDepth((target.getDepth() + 1));
            buildCategoryClosureTable(child, tree);
        }

        for(FoursquareCategory parent : tree){
            parent.setChildId(target.getCategoryId());
            createCategoryClosureTable(parent, (target.getDepth() - parent.getDepth()));
        }

        tree.remove(target);
    }

    private void createCategoryClosureTable(FoursquareCategory foursquareCategory, Integer depth){
        foursquareCategoryDao.createClosureTable(foursquareCategory, depth);
    }

    public void readAllCategoriesWithParentId(String Id){
        foursquareCategoryDao.readAllCategoriesWithParentId(Id);
    }

/* *****************************************************************************************
    FOURSQUARE VENUES
******************************************************************************************** */
    private static final String TAG = "FoursquareSource";
    public void isVenueTableFresh(double lat, double lng, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            Boolean isFresh = foursquareDao.isFresh(lat, lng);
            Log.println(Log.ASSERT, TAG, "isVenueTableFresh " + isFresh);
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

    public void getGeneralFoursquareVenuesNearUserByQuery(@NonNull Double latitude, @NonNull Double longitude, @NonNull String search, @NonNull Observer<List<FoursquareVenue>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchQueryMap(latitude, longitude, search);
        foursquareService.getFoursquareVenuesNearby(searchParameters)
                .map(foursquareSearchResponse -> foursquareSearchResponse.response.venues)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getGeneralFoursquareVenuesNearUserById(@NonNull Double latitude, @NonNull Double longitude, @NonNull String id, @NonNull Observer<List<FoursquareVenue>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildCategoryQueryMap(latitude, longitude, id);
        foursquareService.getFoursquareVenuesNearby(searchParameters)
                .map(foursquareSearchResponse -> foursquareSearchResponse.response.venues)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getRecommendedFoursquareVenuesNearUser(@NonNull Double latitude, @NonNull Double longitude, @NonNull Observer<List<FoursquareResult>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildCoordinatesQueryMap(latitude, longitude);
        foursquareService.getRecommendedFoursquareVenuesNearby(searchParameters)
                .map(foursquareRecommendationsResponse -> foursquareRecommendationsResponse.response.group.results)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getFoursquareVenuesDetails(@NonNull String venueId, @NonNull Observer<FoursquareVenue> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getFoursquareVenueDetails(venueId, searchParameters)
                .map(foursquareDetailsResponse -> foursquareDetailsResponse.response.venue)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getSimilarFoursquareVenuesNearby(@NonNull String venueId, @NonNull Observer<List<FoursquareVenue>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getSimilarFoursquareVenuesNearby(venueId, searchParameters)
                .map(response -> response.response.similarVenues.items)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void createVenue(FoursquareVenue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            FoursquareVenueCategoryCrossRef crossRef = new FoursquareVenueCategoryCrossRef();
            crossRef.category_id = venue.getCategories().get(0).categoryId;
            crossRef.id = venue.id;
            foursquareDao.create(crossRef);
            long result = foursquareDao.create(venue);
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

    public void createRecommendedVenue(FoursquareVenue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            FoursquareVenueCategoryCrossRef crossRef = new FoursquareVenueCategoryCrossRef();
            crossRef.category_id = venue.getCategories().get(0).categoryId;
            crossRef.id = venue.id;
            foursquareDao.create(crossRef);
            long result = foursquareDao.create(venue);
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

    public DataSource.Factory<Integer, FoursquareVenue> readRecommendedVenues(){
        try{
            return executorService.submit(foursquareDao::readRecommendedVenues).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataSource.Factory<Integer, CategoryWithVenues> readAllVenuesWithCategoryIdDataFactory(String categoryId){
        return foursquareCategoryDao.readAllVenuesWithCategoryIdDataFactory(categoryId);
    }

    public void readVenueDetails(String id, Observer<FoursquareVenue> observer){
        Observable<FoursquareVenue> observable = Observable.create(source -> {
            FoursquareVenue result = foursquareDao.readVenueById(id);
            if(result != null){
                source.onNext(result);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void updateVenue(FoursquareVenue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            venue.updateAt = new Date(System.currentTimeMillis());
            int result = foursquareDao.update(venue);
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

    public void updateVenueWithDetails(FoursquareVenue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            venue.updateAt = new Date(System.currentTimeMillis());
            updateVenueContact(venue);
            updateVenueContact(venue);
            updateVenueStats(venue);
            updateVenueDescription(venue);
            updateVenueImage(venue);
            updateVenueHours(venue);
            int result = updateVenueHasDetails(venue, true);
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

    public int updateVenueContact(FoursquareVenue venue){
        return foursquareDao.updateVenueContact(venue.id,
                venue.contact.phone, venue.contact.formattedPhone,
                venue.contact.twitter, venue.contact.instagram,
                venue.contact.facebook, venue.contact.facebookName,
                venue.contact.facebookUsername);
    }

    public int updateVenueStats(FoursquareVenue venue){
        return foursquareDao.updateVenueStats(venue.id, venue.stats.checkinsCount,
                venue.stats.usersCount, venue.stats.tipCount, venue.stats.visitsCount);
    }

    public int updateVenueDescription(FoursquareVenue venue){
        return foursquareDao.updateVenueDescription(venue.id, venue.url,
                venue.rating, venue.ratingColor, venue.ratingSignals, venue.description);
    }

    public int updateVenueImage(FoursquareVenue venue){
        return foursquareDao.updateVenueImage(venue.id, venue.bestPhoto.prefix,
                venue.bestPhoto.suffix, venue.bestPhoto.width, venue.bestPhoto.height,
                venue.bestPhoto.visibility);
    }


    public int updateVenueHours(FoursquareVenue venue){
        return foursquareDao.updateVenueHours(venue.id,
                venue.hours.status, venue.hours.isOpen,
                venue.hours.isLocalHoliday);
    }

    public int updateVenueRecommended(FoursquareVenue venue, boolean isRecommended){
        return foursquareDao.updateVenueRecommended(venue.id, isRecommended);
    }

    public int updateVenueHasDetails(FoursquareVenue venue, boolean hasDetails){
        return foursquareDao.updateVenueRecommended(venue.id, hasDetails);
    }

    public void deleteVenue(FoursquareVenue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            int result = foursquareDao.delete(venue);
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
}
