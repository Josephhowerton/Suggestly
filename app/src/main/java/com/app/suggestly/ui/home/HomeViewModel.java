package com.app.suggestly.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.auth.FirebaseAuth;
import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.foursquare.Venue;
import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.app.model.relations.VenueAndCategory;
import com.app.suggestly.app.model.user.LocationTuple;
import com.app.suggestly.utility.Config;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class HomeViewModel extends AndroidViewModel {
    private final Repository repository;

    public LiveData<Book> topSuggestion;

    public LiveData<PagedList<Book>> fictionBooksPagedList;
    public LiveData<PagedList<Book>> nonFictionBooksPagedList;
    public LiveData<PagedList<VenueAndCategory>> recommendedVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> foodVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> breweryVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> familyVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> activeVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> socialVenuePagedList;
    public LiveData<PagedList<VenueAndCategory>> entertainmentVenuePagedList;

    private final MutableLiveData<LocationTuple> _locationTuple;

    public HomeViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        topSuggestion = repository.readTopSuggestionNewYorkTimesBooksTable();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(10)
                .setEnablePlaceholders(true)
                .build();

        fictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingListHomeFragment(Config.HARD_COVER_FICTION), config).build();
        nonFictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingListHomeFragment(Config.HARD_COVER_NON_FICTION), config).build();
        recommendedVenuePagedList = new LivePagedListBuilder<>(repository.readRecommendedVenuesDataFactoryHomeFragment(), config).build();
        foodVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.FOOD),config).build();
        breweryVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.BREWERY), config).build();
        familyVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.FAMILY_FUN), config).build();
        activeVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.ACTIVE), config).build();
        socialVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.SOCIAL), config).build();
        entertainmentVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryDataFactoryHomeFragment(Config.EVENTS), config).build();
        _locationTuple = new MutableLiveData<>();
//        fetchUserLocation();
    }

    public List<VenueAndCategory> getSavedVenues() {
        return repository.readSavedVenues();
    }

    public List<Book> getSavedBooks() {
        return repository.readSavedBooks();
    }

//    private void fetchUserLocation(){
//        if(FirebaseAuth.getInstance().getCurrentUser() == null){
//            return;
//        }
//        repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .subscribe(new Observer<LocationTuple>() {
//                    Disposable disposable;
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(@NonNull LocationTuple locationTuple) {
//                        _locationTuple.setValue(locationTuple);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        disposable.dispose();
//                    }
//                });
//    }

    public LiveData<LocationTuple> getUserLocation(){
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void storeLastFetchedLocation(double lat, double lng){
        repository.storeLastFetchedLocation(lat, lng);
    }

    public LocationTuple getLastFetchedLocation(double lat, double lng){
        return repository.getLastFetchedLocation(lat, lng);
    }

    public LiveData<Boolean> getRecommendedFoursquareVenuesNearUser(double lat, double lng){
        return repository.getRecommendedFoursquareVenuesNearUser(lat, lng);
    }

    public LiveData<Boolean> getFoursquareVenuesNearUser_FOOD(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FOOD);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUser_BREWERY(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.BREWERY);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.FAMILY_FUN);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_EVENTS(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.EVENTS);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_ACTIVE(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.ACTIVE);
    }

    public LiveData<Boolean> getGeneralFoursquareVenuesNearUserById_SOCIAL(double lat, double lng){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, Config.SOCIAL);
    }

    public long updateVenueSavedInUser(Venue savedVenue, Boolean isSaved){
        return repository.saveBookmarkedVenue(savedVenue, isSaved);
    }

    public long updateBookSavedInUser(Book book, Boolean isSaved){
        return repository.saveBookmarkedBook(book, isSaved);
    }
}