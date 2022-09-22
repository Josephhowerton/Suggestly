package com.app.suggestly.ui.more;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.Suggestion;
import com.app.suggestly.app.model.foursquare.Venue;
import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.app.model.user.LocationTuple;

import java.util.ArrayList;
import java.util.List;

public class MoreViewModel extends AndroidViewModel {
    private final Repository repository;

    public List<Suggestion> saveSuggestions = new ArrayList<>();
    public List<Suggestion> favoriteSuggestions = new ArrayList<>();

    public MoreViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);

        saveSuggestions.addAll(repository.readSavedBooks());
        saveSuggestions.addAll(repository.readSavedVenues());

        favoriteSuggestions.addAll(repository.readFavoriteBooks());
        favoriteSuggestions.addAll(repository.readFavoriteVenues());
    }

    public void storeLastFetchedLocation(double lat, double lng){
        repository.storeLastFetchedLocation(lat, lng);
    }

    public LocationTuple getLastFetchedLocation(double lat, double lng){
        return repository.getLastFetchedLocation(lat, lng);
    }

    public LiveData<LocationTuple> initUserLocation(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return null;
        }
        return repository.readUserLocationLiveData(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public LiveData<List<Suggestion>> initRecommendedVenues(){
        return repository.readRecommendedVenuesLiveData();
    }

    public LiveData<List<Suggestion>> initVenues(String id){
        return repository.readVenuesUsingCategoryIdLiveData(id);
    }

    public LiveData<List<Suggestion>> initBooksByListName(String listName){
        return repository.readNewYorkTimesBestsellingListLiveData(listName);
    }
    public LiveData<Boolean> fetchVenues(double lat, double lng, String id){
        return repository.getGeneralFoursquareVenuesNearUserById(lat, lng, id);
    }

    public long updateVenueFavoriteInUser(Venue savedVenue, Boolean isFavorite){
        return repository.saveFavoriteVenue(savedVenue, isFavorite);
    }

    public long updateBookFavoriteInUser(Book book, Boolean isFavorite){
        return repository.saveFavoriteBook(book, isFavorite);
    }

    public long updateVenueSavedInUser(Venue savedVenue, Boolean isSaved){
        return repository.saveBookmarkedVenue(savedVenue, isSaved);
    }

    public long updateBookSavedInUser(Book book, Boolean isSaved){
        return repository.saveBookmarkedBook(book, isSaved);
    }

}
