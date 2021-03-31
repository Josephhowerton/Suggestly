package com.mortonsworld.suggestly.ui.details;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.CategoryTuple;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;

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

    public List<VenueAndCategory> getSavedVenues() {
        return repository.readSavedVenues();
    }

    public List<VenueAndCategory> getFavoriteVenues() {
        return repository.readFavoriteVenues();
    }

    public List<Book> getSavedBooks() {
        return repository.readSavedBooks();
    }

    public List<Book> getFavoriteBooks() {
        return repository.readFavoriteBooks();
    }

    public LiveData<Venue> readVenueDetails(String id){
        return repository.readVenuesDetails(id);
    }

    public void getFoursquareVenuesDetails(Venue venue){
        repository.getFoursquareVenuesDetails(venue);
    }

    public void getFoursquareVenuesSimilar(Venue venue){
        repository.getFoursquareVenuesSimilar(venue);
    }

    public LiveData<List<VenueAndCategory>> readFoursquareVenuesSimilar(Venue venue){
        return repository.readSimilarVenuesLiveData(venue.getId());
    }

    public LiveData<PagedList<CategoryTuple>> readRelatedCategories(String categoryId){
        return new LivePagedListBuilder<> (repository.readRelatedCategoriesDataFactory(categoryId), config).build();
    }

    public LiveData<Book> readBookDetails(String isbn13){
        return repository.readNewYorkTimesBookUsingISBN13(isbn13);
    }

    public List<Suggestion> readNewYorkTimesBestsellingListLimitThree(String isbn13, String listName){
        return repository.readNewYorkTimesBestsellingListLimitThree(isbn13, listName);
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