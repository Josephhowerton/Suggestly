package com.mortonsworld.suggestly.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;

public class DetailsViewModel extends AndroidViewModel {
    private final Repository repository;

    public DetailsViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<Venue> readVenueDetails(String id){
        return repository.readVenuesDetails(id);
    }

    public LiveData<Book> readBookDetails(String isbn13){
        return repository.readNewYorkTimesBookUsingISBN13(isbn13);
    }
}