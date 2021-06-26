package com.josephhowerton.suggestly.ui.library;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.josephhowerton.suggestly.app.Repository;
import com.josephhowerton.suggestly.app.model.Suggestion;
import com.josephhowerton.suggestly.app.model.nyt.Book;
import com.josephhowerton.suggestly.app.model.relations.VenueAndCategory;
import com.josephhowerton.suggestly.app.model.user.User;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final Repository repository;

    public LiveData<User> userLiveData;

    public LibraryViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userLiveData = repository.readUser(user.getUid());
        }
    }

    public LiveData<List<Suggestion>> readSavedSuggestions(){
        return repository.readSavedSuggestions();
    }

    public LiveData<List<Suggestion>> readFavoriteSuggestions(){
        return repository.readFavoriteSuggestions();
    }

    public void deleteSavedSuggestion(Suggestion suggestions){
        switch (suggestions.getSuggestionType()){
            case BOOK:
                deleteBookInUser((Book) suggestions);
                break;
            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                deleteVenueInUser((VenueAndCategory) suggestions);
                break;
        }
    }

    public int deleteVenueInUser(VenueAndCategory savedVenue){
        return repository.deletedSavedVenue(savedVenue.venue);
    }

    public int deleteBookInUser(Book book){
        return repository.deletedSavedBook(book);
    }


}