package com.mortonsworld.suggest.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.model.foursquare.CategoryWithVenues;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.nyt.Book;
import com.mortonsworld.suggest.utility.Config;

public class HomeViewModel extends AndroidViewModel {
    private final Repository repository;

    public LiveData<Book> topSuggestion;

    public LiveData<PagedList<Book>> fictionBooksPagedList;
    public LiveData<PagedList<Book>> nonFictionBooksPagedList;
    public LiveData<PagedList<FoursquareVenue>> recommendedVenuePagedList;
    public LiveData<PagedList<CategoryWithVenues>> restaurantPagedList;
    public LiveData<PagedList<CategoryWithVenues>> breweryPagedList;
    public LiveData<PagedList<CategoryWithVenues>> familyFunPagedList;
    public LiveData<PagedList<CategoryWithVenues>> activePagedList;
    public LiveData<PagedList<CategoryWithVenues>> socialPagedList;
    public LiveData<PagedList<CategoryWithVenues>> entertainmentPagedList;

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
        recommendedVenuePagedList = new LivePagedListBuilder<>(repository.readRecommendedVenues(), config).build();
        restaurantPagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.FOOD), config).build();
        breweryPagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.BREWERY), config).build();
        familyFunPagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.FAMILY_FUN), config).build();
        activePagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.ACTIVE), config).build();
        socialPagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.SOCIAL), config).build();
        entertainmentPagedList = new LivePagedListBuilder<>(repository.readVenuesWithCategoryId(Config.EVENTS), config).build();
    }

    public void saveSuggestionToLibrary(FoursquareVenue venue){

    }

    public void saveSuggestionToLibrary(Book book){

    }
}