package com.mortonsworld.suggest;

import android.app.Application;
import android.location.Location;

import com.mortonsworld.suggest.interfaces.Suggestion;
import com.mortonsworld.suggest.model.foursquare.CategoryWithVenues;
import com.mortonsworld.suggest.model.foursquare.FoursquareCategory;
import com.mortonsworld.suggest.model.foursquare.FoursquareResult;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.google.GeocodeResponse;
import com.mortonsworld.suggest.model.google.Geometry;
import com.mortonsworld.suggest.model.nyt.Book;
import com.mortonsworld.suggest.source.GoogleSource;
import com.mortonsworld.suggest.source.LocationSource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.mortonsworld.suggest.model.user.LocationTuple;
import com.mortonsworld.suggest.model.user.User;
import com.mortonsworld.suggest.source.FoursquareSource;
import com.mortonsworld.suggest.source.NewYorkTimesSource;
import com.mortonsworld.suggest.source.UserSource;
import com.mortonsworld.suggest.utility.Config;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        foursquareSource = new FoursquareSource(application, service);
        locationSource = new LocationSource(application);
        googleSource = new GoogleSource(application);
        userSource = new UserSource(application);
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

    public LiveData<LocationTuple>  readUserLocation(String id){
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
        foursquareSource.getCategories(new Observer<List<FoursquareCategory>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<FoursquareCategory> categories) {
                for(FoursquareCategory category : categories){
                    List<FoursquareCategory> tree = new ArrayList<>();
                    category.setDepth(0);
                    foursquareSource.buildCategoryClosureTable(category, tree);
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
        foursquareSource.getGeneralFoursquareVenuesNearUserById(lat, lng, categoryId, new Observer<List<FoursquareVenue>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<FoursquareVenue> venues) {
                for(FoursquareVenue foursquareVenue: venues){
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
                    result.venue.isRecommended = true;
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

    private LiveData<Boolean> getFoursquareVenuesDetails(FoursquareVenue venue){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getFoursquareVenuesDetails(venue.id, new Observer<FoursquareVenue>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull FoursquareVenue venue) {
                updateVenueWithDetails(venue);
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

    private LiveData<Boolean> getFoursquareVenuesSimilar(String id){
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        foursquareSource.getSimilarFoursquareVenuesNearby(id, new Observer<List<FoursquareVenue>>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull List<FoursquareVenue> venues) {
                for(FoursquareVenue foursquareVenue: venues){
                    //todo create relationship to other venue;
                    createVenue(foursquareVenue);
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

    public void createVenue(FoursquareVenue venue){
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

    public void createRecommendedVenue(FoursquareVenue venue){
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

    public void updateVenue(FoursquareVenue venue){
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

    public void updateVenueWithDetails(FoursquareVenue venue){
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

    public DataSource.Factory<Integer, FoursquareVenue> readRecommendedVenues(){
        return foursquareSource.readRecommendedVenues();
    }

    public DataSource.Factory<Integer, CategoryWithVenues> readVenuesWithCategoryId(String id){
        return foursquareSource.readAllVenuesWithCategoryIdDataFactory(id);
    }

    public LiveData<FoursquareVenue> readVenuesDetails(String id){
        MutableLiveData<FoursquareVenue> mutableLiveData = new MutableLiveData<>();
        foursquareSource.readVenueDetails(id, new Observer<FoursquareVenue>() {
            Disposable disposable;
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@NonNull FoursquareVenue foursquareVenue) {
                if(!foursquareVenue.hasDetails){

                }

                mutableLiveData.postValue(foursquareVenue);
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

    public DataSource.Factory<Integer, Book> readNewYorkTimesBestsellingList(String listName){
        return newYorkTimesSource.readNewYorkTimesBookList(listName);
    }

    public LiveData<Suggestion> readNewYorkTimesBookRoomDatabase(String id){
        MutableLiveData<Suggestion> mutableLiveData = new MutableLiveData<>();
        newYorkTimesSource.readNewYorkTimesBookUsingISBN(id, new Observer<Book>() {
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

    public void updateNewYorkTimesBookRoomDatabase(Book book){
        newYorkTimesSource.updateNewYorkTimesBook(book, new Observer<Void>() {
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

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void deleteNewYorkTimesBooksListRoomDatabase(Book book){
        newYorkTimesSource.deleteNewYorkTimesBook(book, new Observer<Void>() {
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

}