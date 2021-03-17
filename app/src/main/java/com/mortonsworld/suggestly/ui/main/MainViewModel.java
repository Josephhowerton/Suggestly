package com.mortonsworld.suggestly.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.mortonsworld.suggestly.Repository;


public class MainViewModel extends AndroidViewModel{
    private final Repository repository;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void moreRecommendedSuggestions(){

    }

    public void moreVenueSuggestions(String categoryId){

    }

    public void moreBookSuggestions(String listName){

    }

    public void fetchSuggestionVenueDetails(String id){

    }

    public void fetchSuggestionBookDetails(String id){

    }
}
