package com.mortonsworld.suggestly.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.nyt.Book;
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

    public HomeViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        topSuggestion = repository.readTopSuggestionNewYorkTimesBooksTable();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(5)
                .setInitialLoadSizeHint(10)
                .setEnablePlaceholders(true)
                .build();

        fictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingList(Config.HARD_COVER_FICTION), config).build();
        nonFictionBooksPagedList = new LivePagedListBuilder<>(repository.readNewYorkTimesBestsellingList(Config.HARD_COVER_NON_FICTION), config).build();
        recommendedVenuePagedList = new LivePagedListBuilder<>(repository.readRecommendedVenuesDataFactory(), config).build();
        foodVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.FOOD), config).build();
        breweryVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.BREWERY), config).build();
        familyVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.FAMILY_FUN), config).build();
        activeVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.ACTIVE), config).build();
        socialVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.SOCIAL), config).build();
        entertainmentVenuePagedList = new LivePagedListBuilder<>(repository.readVenuesUsingCategoryId(Config.EVENTS), config).build();

    }

    public void saveSuggestionToLibrary(Venue venue){

    }

    public void saveSuggestionToLibrary(Book book){

    }
}