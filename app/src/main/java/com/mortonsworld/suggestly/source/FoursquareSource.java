package com.mortonsworld.suggestly.source;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;

import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.CategoryClosure;
import com.mortonsworld.suggestly.model.foursquare.FoursquareResult;
import com.mortonsworld.suggestly.model.foursquare.SimilarVenues;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.foursquare.VenueAndCategory;
import com.mortonsworld.suggestly.room.FoursquareCategoryDao;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.retrofit.FoursquareManager;
import com.mortonsworld.suggestly.retrofit.FoursquareService;
import com.mortonsworld.suggestly.retrofit.ServiceFactory;
import com.mortonsworld.suggestly.room.FoursquareDao;
import com.mortonsworld.suggestly.room.RoomDB;

import java.sql.Date;
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

    public void getCategories(Observer<List<Category>> observer){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getFoursquareCategories(searchParameters)
                .map(response -> response.response.categories)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void buildCategoryClosureTable(List<Category> tree, Category target, int depth){
        tree.add(target);

        while (target.hasChildren()){
            Category child = target.removeChild();
            buildCategoryClosureTable(tree, child,depth + 1);
        }

        for(Category parent : tree){
            CategoryClosure categoryClosure = new CategoryClosure(parent.getId(), target.getId(), depth--);
            createCategoryClosureTable(categoryClosure);
        }

        createCategoryBaseTable(target);
        tree.remove(target);
    }

    private void createCategoryBaseTable(Category category){
        foursquareCategoryDao.createBaseTable(category);
    }

    private void createCategoryClosureTable(CategoryClosure categoryClosure){
        foursquareCategoryDao.createClosureTable(categoryClosure);
    }

    public void readAllCategoriesWithParentId(String Id){
        foursquareCategoryDao.readAllCategoriesWithParentId(Id);
    }

    public LiveData<Category> getFoursquareCategoryName(String categoryId){
        try{
            return executorService.submit(() -> foursquareCategoryDao.readFoursquareCategory(categoryId)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    /* *****************************************************************************************
    FOURSQUARE VENUES
******************************************************************************************** */
    public void isVenueTableFresh(double lat, double lng, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            Boolean isFresh = foursquareDao.isFresh(lat, lng);
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

    public void getGeneralFoursquareVenuesNearUserByQuery(@NonNull Double latitude, @NonNull Double longitude, @NonNull String search, @NonNull Observer<List<Venue>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchQueryMap(latitude, longitude, search);
        foursquareService.getFoursquareVenuesNearby(searchParameters)
                .map(foursquareSearchResponse -> foursquareSearchResponse.response.venues)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getGeneralFoursquareVenuesNearUserById(@NonNull Double latitude, @NonNull Double longitude, @NonNull String id, @NonNull Observer<List<Venue>> placeObserver){
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

    public void getFoursquareVenuesDetails(@NonNull String venueId, @NonNull Observer<Venue> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getFoursquareVenueDetails(venueId, searchParameters)
                .map(foursquareDetailsResponse -> foursquareDetailsResponse.response.venue)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void getSimilarFoursquareVenuesNearby(@NonNull String venueId, @NonNull Observer<List<Venue>> placeObserver){
        HashMap<String, String> searchParameters = FoursquareManager.buildSearchWithCoordinatesQueryMap();
        foursquareService.getSimilarFoursquareVenuesNearby(venueId, searchParameters)
                .map(response -> response.response.similarVenues.items)
                .subscribeOn(Schedulers.io())
                .subscribe(placeObserver);
    }

    public void createVenue(Venue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
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

    public void createSimilarVenue(SimilarVenues venue){
        executorService.execute(() -> foursquareDao.createSimilarVenue(venue));
    }

    public void createRecommendedVenue(Venue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
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

    public DataSource.Factory<Integer, VenueAndCategory> readRecommendedVenuesDataFactory(){
        try{
            return executorService.submit(foursquareDao::readRecommendedVenuesDataFactory).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataSource.Factory<Integer, VenueAndCategory> readVenuesUsingCategoryIdDataFactory(String categoryId){
        try{
            return executorService.submit(() -> foursquareDao.readVenueByCategoryId(categoryId)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DataSource.Factory<Integer, Category> readRelatedCategoriesDataFactory(String categoryId){
        try{
            return executorService.submit(() -> foursquareCategoryDao.readRelatedCategories(categoryId)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VenueAndCategory> readRecommendedVenuesLiveData(){
        try{
            return executorService.submit(foursquareDao::readRecommendedVenuesLiveData).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<VenueAndCategory>> readSimilarVenuesLiveData(String id){
        try{
            return executorService.submit(() -> foursquareDao.readSimilarVenuesLiveData(id)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readVenuesObservable(String categoryId, Observer<List<VenueAndCategory>> observer){
        foursquareDao.readVenueByCategoryIdObservable(categoryId)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public LiveData<Venue> readVenueDetails(String id){
        try{
            return executorService.submit(() -> foursquareDao.readVenueById(id)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateVenue(Venue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            venue.venueUpdatedDate = new Date(System.currentTimeMillis());
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

    public void updateVenueDistance(double lat, double lng){
        executorService.execute(() ->foursquareDao.updateVenueDistance(lat, lng));
    }

    public void updateVenueWithDetails(Venue venue, Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            venue.venueUpdatedDate = new Date(System.currentTimeMillis());
            updateVenueHasDetails(venue, true);
            updateVenueContact(venue);
            updateVenueStats(venue);
            updateVenueDescription(venue);
            updateVenueImage(venue);
            int result = updateVenueHours(venue);
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

    public int updateVenueContact(Venue venue){
        return foursquareDao.updateVenueContact(venue.venueId,
                venue.contact.phone, venue.contact.formattedPhone,
                venue.contact.twitter, venue.contact.instagram,
                venue.contact.facebook, venue.contact.facebookName,
                venue.contact.facebookUsername);
    }

    public int updateVenueStats(Venue venue){
        return foursquareDao.updateVenueStats(venue.venueId, venue.stats.checkinsCount,
                venue.stats.usersCount, venue.stats.tipCount, venue.stats.visitsCount);
    }

    public int updateVenueDescription(Venue venue){
        return foursquareDao.updateVenueDescription(venue.venueId, venue.url,
                venue.rating, venue.ratingColor, venue.ratingSignals, venue.description);
    }

    public int updateVenueImage(Venue venue){
        return foursquareDao.updateVenueImage(venue.venueId, venue.bestPhoto.prefix,
                venue.bestPhoto.suffix, venue.bestPhoto.width, venue.bestPhoto.height,
                venue.bestPhoto.visibility);
    }


    public int updateVenueHours(Venue venue){
        return foursquareDao.updateVenueHours(venue.venueId,
                venue.hours.status, venue.hours.isOpen,
                venue.hours.isLocalHoliday);
    }

    public int updateVenueRecommended(Venue venue, boolean isRecommended){
        return foursquareDao.updateVenueRecommended(venue.venueId, isRecommended);
    }

    public int updateVenueHasDetails(Venue venue, boolean hasDetails){
        return foursquareDao.updateVenueHasDetails(venue.venueId, hasDetails);
    }

    public void deleteVenue(Venue venue, Observer<Boolean> observer){
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
