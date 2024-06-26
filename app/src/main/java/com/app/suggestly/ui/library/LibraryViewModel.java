package com.app.suggestly.ui.library;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.suggestly.app.Repository;
import com.app.suggestly.app.model.Suggestion;
import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.app.model.relations.VenueAndCategory;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final Repository repository;

    public LibraryViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
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