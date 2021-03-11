package com.mortonsworld.suggest.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.nyt.Book;

public class DetailsViewModel extends AndroidViewModel {
    private Repository repository;

    public LiveData<Book> bookLiveData;
    public LiveData<FoursquareVenue> venueLiveData;

    public DetailsViewModel(Application application){
        super(application);

    }

}