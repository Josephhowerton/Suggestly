package com.mortonsworld.suggestly.ui.explore;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mortonsworld.suggestly.Repository;

public class ExploreViewModel extends AndroidViewModel {
    private Repository repository;

    public ExploreViewModel(Application application) {
        super(application);

        repository = Repository.getInstance(application);
    }

    public void search(String query){

    }
}