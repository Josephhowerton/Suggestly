package com.app.suggestly.app.source;

import android.app.Application;

import androidx.paging.DataSource;

import com.app.suggestly.app.model.foursquare.Venue;
import com.app.suggestly.app.model.relations.SearchTuple;
import com.app.suggestly.app.network.retrofit.FoursquareManager;
import com.app.suggestly.app.network.retrofit.FoursquareService;
import com.app.suggestly.app.network.retrofit.ServiceFactory;
import com.app.suggestly.app.room.RoomDB;
import com.app.suggestly.app.room.SearchDao;
import com.app.suggestly.utility.Config;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class SearchSource {
    private final SearchDao searchDao;
    private final PublishSubject<String> publishSubject;

    private final FoursquareService foursquareService;
    private final ExecutorService executorService;

    public SearchSource(Application application){
        foursquareService = ServiceFactory.getFoursquareClient(Config.FOURSQUARE_BASE_URL, FoursquareService.class);

        searchDao = RoomDB.getInstance(application).getSearchDAO();
        publishSubject = PublishSubject.create();

        executorService = Executors.newFixedThreadPool(10);
    }

    public DataSource.Factory<Integer, SearchTuple> suggestlySearch(String search){
        publishSubject.onNext(search);
        try{
            return executorService.submit(() -> searchDao.venueSearch(formatString(search))).get();
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }

        return null;
    }

    public void removeSuggestlySearch(){
        publishSubject.onComplete();
    }

    public void initializeVenueSearch(Double lat, Double lng, Observer<List<Venue>> observer){
        publishSubject.debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMap((Function<String, ObservableSource<List<Venue>>>) s -> {
                    HashMap<String, String> queryMap = FoursquareManager.buildSearchQueryMap(lat, lng, s);
                    return foursquareService.getFoursquareVenuesNearby(queryMap)
                            .map(foursquareSearchResponse -> foursquareSearchResponse.response.venues)
                            .subscribeOn(Schedulers.io());
                })
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    @NotNull
    private String formatString(@NotNull String search){
        return "%"+search.replace(" ", "%")+"%";
    }

}
