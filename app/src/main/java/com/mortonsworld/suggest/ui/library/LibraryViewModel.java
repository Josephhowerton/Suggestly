package com.mortonsworld.suggest.ui.library;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.mortonsworld.suggest.Repository;
import com.mortonsworld.suggest.interfaces.Suggestion;
import com.mortonsworld.suggest.model.foursquare.CategoryWithVenues;
import com.mortonsworld.suggest.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final Repository repository;

    public LiveData<User> userLiveData;
    public LiveData<PagedList<CategoryWithVenues>> savedSuggestions;
    public LiveData<PagedList<CategoryWithVenues>> favoritesSuggestions;

    public LibraryViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userLiveData = repository.readUser(user.getUid());
    }

    public LiveData<Boolean> deleteSavedSuggestion(String id, List<Suggestion> suggestions){
        return repository.deleteUserSavedSuggestion(id, suggestions);
    }

    public LiveData<Boolean> deleteFavoriteSuggestion(String id, List<Suggestion> suggestions){
        return repository.deleteUserFavoriteSuggestion(id, suggestions);
    }
}