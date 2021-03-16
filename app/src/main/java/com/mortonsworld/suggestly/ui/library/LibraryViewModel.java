package com.mortonsworld.suggestly.ui.library;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.mortonsworld.suggestly.Repository;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final Repository repository;

    public LiveData<User> userLiveData;
    public LiveData<PagedList<Suggestion>> savedSuggestions;

    public LibraryViewModel(Application application) {
        super(application);
        repository = Repository.getInstance(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userLiveData = repository.readUser(user.getUid());
        }
    }

    public LiveData<Boolean> deleteSavedSuggestion(String id, List<Suggestion> suggestions){
        return repository.deleteUserSavedSuggestion(id, suggestions);
    }

    public LiveData<Boolean> deleteFavoriteSuggestion(String id, List<Suggestion> suggestions){
        return repository.deleteUserFavoriteSuggestion(id, suggestions);
    }
}