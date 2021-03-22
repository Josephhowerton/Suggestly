package com.mortonsworld.suggestly.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.auth.FirebaseAuth;
import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.foursquare.VenueAndCategory;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.utility.Config;

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
    private MutableLiveData<Boolean> load = new MutableLiveData<>();
    public HomeViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        topSuggestion = repository.readTopSuggestionNewYorkTimesBooksTable();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(4)
                .setInitialLoadSizeHint(5)
                .setEnablePlaceholders(true)
                .build();

        fictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingList(Config.HARD_COVER_FICTION), config).build();
        nonFictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingList(Config.HARD_COVER_NON_FICTION), config).build();
        recommendedVenuePagedList = new LivePagedListBuilder<>(repository.readRecommendedVenuesDataFactory(), config).build();
        foodVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.FOOD),config).build();
        breweryVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.BREWERY), config).build();
        familyVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.FAMILY_FUN), config).build();
        activeVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.ACTIVE), config).build();
        socialVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.SOCIAL), config).build();
        entertainmentVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.EVENTS), config).build();

    }

    public LiveData<LocationTuple> getUserLocation(){
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void storeLastFetchedLocation(double lat, double lng){
        repository.storeLastFetchedLocation(lat, lng);
    }

    public LocationTuple getLastFetchedLocation(double lat, double lng){
        return repository.getLastFetchedLocation(lat, lng);
    }


    public void saveSuggestionToLibrary(Venue venue){

    }

    public void saveSuggestionToLibrary(Book book){

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

    public LiveData<Boolean> getLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load.setValue(load);
    }
}