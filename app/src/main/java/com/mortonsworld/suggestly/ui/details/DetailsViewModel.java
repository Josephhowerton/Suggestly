package com.mortonsworld.suggestly.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.foursquare.VenueAndCategory;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.utility.Config;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel {
    private final Repository repository;
    private final PagedList.Config config;
    public DetailsViewModel(Application application){
        super(application);
        repository = Repository.getInstance(application);

        config = new PagedList.Config.Builder()
                .setPageSize(4)
                .setInitialLoadSizeHint(5)
                .setEnablePlaceholders(true)
                .build();
    }

    public LiveData<Venue> readVenueDetails(String id){
        return repository.readVenuesDetails(id);
    }

    public LiveData<Boolean> getFoursquareVenuesDetails(Venue venue){
        return repository.getFoursquareVenuesDetails(venue);
    }

    public LiveData<Boolean> getFoursquareVenuesSimilar(Venue venue){
        return repository.getFoursquareVenuesSimilar(venue);
    }

    public LiveData<List<VenueAndCategory>> readFoursquareVenuesSimilar(Venue venue){
        return repository.readSimilarVenuesLiveData(venue.getId());
    }

    public LiveData<PagedList<Category>> readRelatedCategories(String categoryId){
        return new LivePagedListBuilder<> (repository.readRelatedCategoriesDataFactory(categoryId), config).build();
    }

    public LiveData<Book> readBookDetails(String isbn13){
        return repository.readNewYorkTimesBookUsingISBN13(isbn13);
    }

    public List<Suggestion> readNewYorkTimesBestsellingListLimitThree(String isbn13, String listName){
        return repository.readNewYorkTimesBestsellingListLimitThree(isbn13, listName);
    }


}