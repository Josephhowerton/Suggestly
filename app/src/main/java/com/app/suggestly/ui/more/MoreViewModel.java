package com.app.suggestly.ui.more;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.Suggestion;
import com.app.suggestly.app.model.foursquare.Venue;
import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.app.model.user.LocationTuple;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MoreViewModel extends AndroidViewModel {
    private final Repository repository;

    public List<Suggestion> saveSuggestions = new ArrayList<>();
    public List<Suggestion> favoriteSuggestions = new ArrayList<>();

    private final MutableLiveData<LocationTuple> _locationTuple;

    public MoreViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);

        saveSuggestions.addAll(repository.readSavedBooks());
        saveSuggestions.addAll(repository.readSavedVenues());

        favoriteSuggestions.addAll(repository.readFavoriteBooks());
        favoriteSuggestions.addAll(repository.readFavoriteVenues());

        _locationTuple = new MutableLiveData<LocationTuple>();
//        fetchUserLocation();
    }

    public void storeLastFetchedLocation(double lat, double lng){
        repository.storeLastFetchedLocation(lat, lng);
    }

    public LocationTuple getLastFetchedLocation(double lat, double lng){
        return repository.getLastFetchedLocation(lat, lng);
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
