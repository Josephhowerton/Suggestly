package com.app.suggestly.ui.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.suggestly.app.Repository;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class ManualLocationViewModel extends AndroidViewModel{
    private final Repository repository;

    public ManualLocationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public void searchManualLocationInput(String target){
        repository.fetchManualLocationInput(target);
    }

    public LiveData<List<AutocompletePrediction>> listenForAutoCompleteResults(){
        return repository.subscribeToAddressAutoCompletePredictions();
    }

    public LiveData<Boolean> convertAddressToCoordinates(String target){
        return repository.convertManualLocationInputToCoordinates(target);
    }
}