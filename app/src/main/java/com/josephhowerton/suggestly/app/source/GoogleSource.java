package com.josephhowerton.suggestly.app.source;

import java.util.List;
import java.util.HashMap;
import android.app.Application;
import androidx.annotation.NonNull;

import com.josephhowerton.suggestly.app.model.google.GeocodeResponse;
import com.josephhowerton.suggestly.app.network.retrofit.GoogleManager;
import com.josephhowerton.suggestly.app.network.retrofit.GoogleService;
import com.josephhowerton.suggestly.app.network.retrofit.ServiceFactory;
import com.josephhowerton.suggestly.utility.Config;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class GoogleSource implements OnSuccessListener<FindAutocompletePredictionsResponse>,
        OnFailureListener {
    private final BehaviorSubject<List<AutocompletePrediction>> predictionsBehaviorSubject;
    private final GoogleService googleService;
    private final PlacesClient placesClient;

    public GoogleSource(Application application){
        predictionsBehaviorSubject = BehaviorSubject.create();
        Places.initialize(application, Config.GOOGLE_API_KEY);
        placesClient = Places.createClient(application);
        googleService = ServiceFactory.getGeoCodeClient(Config.GEO_CODING_BASE_URL, GoogleService.class);
    }

    public void subscribeToManualLocationAutoCompleteResponse(Observer<List<AutocompletePrediction>> observer){
        predictionsBehaviorSubject.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void fetchAddressAutoCompleteResults(String target){
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setQuery(target)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(this)
                .addOnFailureListener(this);
    }

    public void fetchCoordinatesForAddress(String target, Observer<GeocodeResponse> observer){
        HashMap<String, String> map = GoogleManager.buildQueryMap(target);
        googleService.fetchCoordinatesUsingAddress(map)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    @Override
    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
        predictionsBehaviorSubject.onNext(findAutocompletePredictionsResponse.getAutocompletePredictions());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if(e instanceof ApiException){
            ApiException exception = (ApiException) e;
            exception.printStackTrace();
        }else{
            e.printStackTrace();
        }
    }
}
