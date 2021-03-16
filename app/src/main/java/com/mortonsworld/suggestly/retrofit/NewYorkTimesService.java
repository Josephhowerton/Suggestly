package com.mortonsworld.suggestly.retrofit;

import com.mortonsworld.suggestly.model.nyt.BookResponse;
import java.util.HashMap;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface NewYorkTimesService {
    @GET("/svc/books/v3/lists/current/{listName}.json")
    Observable<BookResponse> fetchBestsellingBooksByListName(@Path("listName") String name, @QueryMap HashMap<String, String> argumentMap);

    @GET("/maps/api/geocode/overview.json")
    Observable<BookResponse> fetchBestsellingBooksAllList(@QueryMap HashMap<String, String> argumentMap);
}