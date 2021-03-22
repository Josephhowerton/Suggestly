package com.mortonsworld.suggestly;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.FoursquareResult;
import com.mortonsworld.suggestly.model.foursquare.SimilarVenues;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.foursquare.VenueAndCategory;
import com.mortonsworld.suggestly.model.google.GeocodeResponse;
import com.mortonsworld.suggestly.model.google.Geometry;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.source.GoogleSource;
import com.mortonsworld.suggestly.source.LocationSource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.model.user.User;
import com.mortonsworld.suggestly.source.FoursquareSource;
import com.mortonsworld.suggestly.source.NewYorkTimesSource;
import com.mortonsworld.suggestly.source.UserSource;
import com.mortonsworld.suggestly.utility.Config;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class Repository{
    private static final String TAG = "Repository";
    private static Repository INSTANCE;
    private NewYorkTimesSource newYorkTimesSource;
    private SharedPreferences sharedPreferences;
    private FoursquareSource foursquareSource;
    private LocationSource locationSource;
    private GoogleSource googleSource;
    private UserSource userSource;

    private Repository(Application application){
        ExecutorService service = Executors.newFixedThreadPool(10);
        FirebaseApp.initializeApp(application);
        initializeSources(application, service);
        initializeLocationObservable();
        buildFoursquareCategoryTableIfEmpty();
        fetchNewYorkTimesBooksIfNotFresh();
    }

    public static Repository getInstance(Application application){
        if(INSTANCE == null){
            INSTANCE = new Repository(application);
        }
        return INSTANCE;
    }
/* ********************************************************************************************
   Initialization
*********************************************************************************************** */
    public void initializeSources(Application application, ExecutorService service){
        newYorkTimesSource = new NewYorkTimesSource(application, service);
        sharedPreferences = application.getSharedPreferences(Config.USER_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        foursquareSource = new FoursquareSource(application, service);
        locationSource = new LocationSource(application);
        googleSource = new GoogleSource(application);
        userSource = new UserSource(application, service);
    }

    public void initializeLocationObservable(){
        locationSource.subscribeToLocationUpdates(new Observer<Location>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Location location) {
                updateUserLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), location.getLatitude(), location.getLongitude());
                updateVenueDistance(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }
/* ********************************************************************************************
    Location
*********************************************************************************************** */
    public Boolean isLocationUpdatesActive() {
        return locationSource.isLocationUpdatesActive();
    }

    public void enableLocationServices() {
        locationSource.requestLocationUpdates();
    }

    public void disableLocationServices() {
        locationSource.unsubscribeToLocationUpdates();
    }

    public void storeLastFetchedLocation(double lat, double lng){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.USER_SHARED_PREFERENCE_PREVIOUS_LATITUDE, String.valueOf(lat));
        editor.putString(Config.USER_SHARED_PREFERENCE_PREVIOUS_LONGITUDE, String.valueOf(lng));
        editor.apply();
    }

    public LocationTuple getLastFetchedLocation(double lat, double lng){
        LocationTuple locationTuple = new LocationTuple();
        locationTuple.lat = Double.parseDouble(sharedPreferences.getString(Config.USER_SHARED_PREFERENCE_PREVIOUS_LATITUDE, String.valueOf(lat)));
        locationTuple.lng = Double.parseDouble(sharedPreferences.getString(Config.USER_SHARED_PREFERENCE_PREVIOUS_LONGITUDE, String.valueOf(lng)));
        return locationTuple;
    }

    public LiveData<List<AutocompletePrediction>> subscribeToAddressAutoCompletePredictions(){
        MutableLiveData<List<AutocompletePrediction>> predictions = new MutableLiveData<>();
        googleSource.subscribeToManualLocationAutoCompleteResponse(new Observer<List<AutocompletePrediction>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<AutocompletePrediction> autocompletePredictions) {
                predictions.postValue(autocompletePredictions);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return predictions;
    }

    public void fetchManualLocationInput(String target){
        googleSource.fetchAddressAutoCompleteResults(target);
    }

    public LiveData<Boolean> convertManualLocationInputToCoordinates(String target){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        googleSource.fetchCoordinatesForAddress(target, new Observer<GeocodeResponse>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull GeocodeResponse geocodeResponse) {
                Geometry geometry = geocodeResponse.results.get(0).geometry;
                updateUserLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), geometry.location.lat, geometry.location.lng);
                updateVenueDistance(geometry.location.lat, geometry.location.lng);
                mutableLiveData.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                mutableLiveData.postValue(false);
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }
/* ********************************************************************************************
    User
*********************************************************************************************** */
    public LiveData<Boolean> authenticateUser(IdpResponse response){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(response != null && firebaseUser != null){
            mutableLiveData.setValue(!response.isNewUser());
        }else{
            mutableLiveData.setValue(false);
        }
        return mutableLiveData;
    }

    public LiveData<Boolean> checkIfUserInRoom(String id){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.checkIfUserInRoom(id, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> createUser(User user){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.createUser(user, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<User> readUser(String id){
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        userSource.readUser(id, new Observer<User>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull User user) {
                mutableLiveData.postValue(user);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<LocationTuple> readUserLocationLiveData(String id){
        MutableLiveData<LocationTuple> mutableLiveData = new MutableLiveData<>();
        userSource.readUserLocation(id, new Observer<LocationTuple>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull LocationTuple locationTuple) {
                mutableLiveData.postValue(locationTuple);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LocationTuple readUserLocation(String id){
        return userSource.readUserLocation(id);
    }

    public LiveData<Boolean> updateUser(User user){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.updateUser(user, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> updateUserLocation(String id, double lat, double lng){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.updateUserLocation(id, lat, lng, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> updateUserLatestSignIn(String id, Date latestSignIn){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.updateUserLastSignedIn(id, latestSignIn, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> deleteUserSavedSuggestion(String id, List<Suggestion> suggestions){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.updateUserSavedSuggestions(id, suggestions, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> deleteUserFavoriteSuggestion(String id, List<Suggestion> suggestions){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.updateUserFavoriteSuggestions(id, suggestions, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> deleteUser(){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        userSource.deleteUser(new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

/* ********************************************************************************************
    Foursquare Category
*********************************************************************************************** */
    public void buildFoursquareCategoryTableIfEmpty(){
        foursquareSource.isCategoryTableEmpty(new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean isEmpty) {
                if(isEmpty){
                    getFoursquareCategories();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public void getFoursquareCategories(){
        foursquareSource.getCategories(new Observer<List<Category>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<Category> categories) {
                for(Category category : categories){
                    List<Category> tree = new ArrayList<>();
                    foursquareSource.buildCategoryClosureTable(tree, category,0);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public LiveData<Category> getFoursquareCategoryName(String id){
        return foursquareSource.getFoursquareCategoryName(id);
    }

    public DataSource.Factory<Integer, Category>readRelatedCategoriesDataFactory(String categoryId){
        return foursquareSource.readRelatedCategoriesDataFactory(categoryId);
    }
/* ********************************************************************************************
    Foursquare Venues
*********************************************************************************************** */
    public LiveData<Boolean> isVenueTableFresh(double lat, double lng){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.isVenueTableFresh(lat, lng, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                mutableLiveData.postValue(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById(double lat, double lng, String categoryId){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getGeneralFoursquareVenuesNearUserById(lat, lng, categoryId, new Observer<List<Venue>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<Venue> venues) {
                for(Venue foursquareVenue: venues){
                    foursquareVenue.categoryId = foursquareVenue.categories.get(0).id;
                    createVenue(foursquareVenue);
                }
                mutableLiveData.postValue(venues.size() > 0);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> getRecommendedFoursquareVenuesNearUser(double lat, double lng){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getRecommendedFoursquareVenuesNearUser(lat, lng, new Observer<List<FoursquareResult>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<FoursquareResult> venues) {
                for(FoursquareResult result: venues){
                    String categoryId = result.venue.categories.get(0).id;
                    result.venue.isRecommended = true;
                    result.venue.categoryId = categoryId;
                    createRecommendedVenue(result.venue);
                }
                mutableLiveData.postValue(venues.size() > 0);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return mutableLiveData;
    }

    @NotNull
    public LiveData<Boolean> getFoursquareVenuesDetails(Venue venue){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getFoursquareVenuesDetails(venue.venueId, new Observer<Venue>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Venue venue) {
                mutableLiveData.postValue(true);
                updateVenueWithDetails(venue);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveData.postValue(false);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return mutableLiveData;
    }

    @NotNull
    public LiveData<Boolean> getFoursquareVenuesSimilar(Venue venue){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getSimilarFoursquareVenuesNearby(venue.venueId, new Observer<List<Venue>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<Venue> venues) {
                for(Venue foursquareVenue: venues){
                    //todo create relationship to other venue;
                    foursquareVenue.categoryId = foursquareVenue.categories.get(0).id;
                    createVenue(foursquareVenue);

                    createSimilarVenue(venue, foursquareVenue);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return mutableLiveData;
    }

    public void createVenue(Venue venue){
        foursquareSource.createVenue(venue, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if(!aBoolean){
                    updateVenue(venue);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public void createSimilarVenue(Venue owner, Venue sibling){
        SimilarVenues similarVenues = new SimilarVenues();
        similarVenues.ownerId = owner.venueId;
        similarVenues.siblingId = sibling.venueId;
        foursquareSource.createSimilarVenue(similarVenues);
    }

    public void createRecommendedVenue(Venue venue){
        foursquareSource.createRecommendedVenue(venue, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if(!aBoolean){
                    foursquareSource.updateVenueRecommended(venue, true);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public void updateVenue(Venue venue){
        foursquareSource.updateVenue(venue, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public void updateVenueDistance(double lat, double lng){
        foursquareSource.updateVenueDistance(lat, lng);
    }

    public void updateVenueWithDetails(Venue venue){
        foursquareSource.updateVenueWithDetails(venue, new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public DataSource.Factory<Integer, VenueAndCategory> readRecommendedVenuesDataFactory(){
        return foursquareSource.readRecommendedVenuesDataFactory();
    }

    public DataSource.Factory<Integer, VenueAndCategory> readVenuesUsingCategoryId(String categoryId){
        return foursquareSource.readVenuesUsingCategoryIdDataFactory(categoryId);
    }

    public LiveData<List<Suggestion>> readRecommendedVenuesLiveData(){
        MutableLiveData<List<Suggestion>> mutableLiveData = new MutableLiveData<>();
        List<Suggestion> suggestions = new ArrayList<>(foursquareSource.readRecommendedVenuesLiveData());
        mutableLiveData.postValue(suggestions);
        return mutableLiveData;
    }

    public LiveData<List<VenueAndCategory>> readSimilarVenuesLiveData(String id){
        return foursquareSource.readSimilarVenuesLiveData(id);
    }

    public LiveData<List<Suggestion>> readVenuesUsingCategoryIdLiveData(String categoryId){
        MutableLiveData<List<Suggestion>> mutableLiveData = new MutableLiveData<>();
        foursquareSource.readVenuesObservable(categoryId, new Observer<List<VenueAndCategory>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<VenueAndCategory> venueAndCategories) {
                mutableLiveData.postValue(new ArrayList<>(venueAndCategories));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return mutableLiveData;
    }

    public LiveData<Venue> readVenuesDetails(String id){
        return foursquareSource.readVenueDetails(id);
    }

    /* ********************************************************************************************
        Foursquare Venues
    *********************************************************************************************** */
    public LiveData<Boolean> fetchNewYorkTimesBooksIfNotFresh(){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        newYorkTimesSource.isNewYorkTimesTableFresh(new Observer<Boolean>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if(!aBoolean){
                    fetchNewYorkTimesBestsellingByListName(Config.HARD_COVER_NON_FICTION);
                    fetchNewYorkTimesBestsellingByListName(Config.HARD_COVER_FICTION);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveData.postValue(false);
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

        return mutableLiveData;
    }

    public LiveData<Boolean> fetchNewYorkTimesBestsellingByListName(String listName){
        MutableLiveData<Boolean> books = new MutableLiveData<>();
        newYorkTimesSource.fetchNewYorkTimesBestsellingByListName(listName, new Observer<List<Book>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<Book> newYorkTimesBooks) {
                insertNewYorkTimesBookListRoomDatabase(newYorkTimesBooks);
                books.postValue(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                books.postValue(false);
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return books;
    }

    public void insertNewYorkTimesBookListRoomDatabase(List<Book> books){
        newYorkTimesSource.insertNewYorkTimesBookListWithTimeStamp(books, new Observer<Void>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Void aVoid) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    public LiveData<Book> readTopSuggestionNewYorkTimesBooksTable(){
        MutableLiveData<Book> mutableLiveData = new MutableLiveData<>();
        newYorkTimesSource.readTopSuggestionBook(new Observer<Book>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Book book) {
                mutableLiveData.postValue(book);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public LiveData<Book> readNewYorkTimesBookUsingISBN13(String isbn13){
        MutableLiveData<Book> mutableLiveData = new MutableLiveData<>();
        newYorkTimesSource.readTopBookUsingISBN13(isbn13, new Observer<Book>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Book book) {
                mutableLiveData.postValue(book);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        return mutableLiveData;
    }

    public DataSource.Factory<Integer, Book> readNewYorkTimesBestsellingList(String listName){
        return newYorkTimesSource.readNewYorkTimesBookListDataFactory(listName);
    }

    public LiveData<List<Suggestion>> readNewYorkTimesBestsellingListLiveData(String listName){
        MutableLiveData<List<Suggestion>> mutableLiveData = new MutableLiveData<>();
        List<Suggestion> suggestions = new ArrayList<>(newYorkTimesSource.readBooksByListName(listName));
        mutableLiveData.postValue(suggestions);
        return mutableLiveData;
    }

    public List<Suggestion> readNewYorkTimesBestsellingListLimitThree(String isbn13, String listName){
        return new ArrayList<>(newYorkTimesSource.readBooksByListNameLimitThree(isbn13, listName));
    }

}