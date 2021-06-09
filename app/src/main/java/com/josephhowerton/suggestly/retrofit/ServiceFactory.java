package com.josephhowerton.suggestly.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();
    private static final GsonConverterFactory converterFactory = GsonConverterFactory.create(gson);

    private static Retrofit retrofitFoursquare;
    private static final Retrofit.Builder retrofitBuilderFoursquare = new Retrofit.Builder();

    private static Retrofit retrofit_Google;
    private static final Retrofit.Builder googleBuilder = new Retrofit.Builder();

    private static Retrofit retrofit_NYT;
    private static final Retrofit.Builder nytBuilder = new Retrofit.Builder();

    private static final RxJava3CallAdapterFactory adapterFactory = RxJava3CallAdapterFactory.createWithScheduler(Schedulers.computation());

    private static OkHttpClient okHttpClient;

    @NotNull
    public static <service> service getFoursquareClient(String baseUrl, Class<service> serviceClass){

        if(okHttpClient == null){
            initializeOkHttp();
        }

        if(retrofitFoursquare == null){
            retrofitFoursquare = retrofitBuilderFoursquare
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(adapterFactory)
                    .build();
        }

        return retrofitFoursquare.create(serviceClass);
    }

    @NotNull
    public static <service> service getGeoCodeClient(String baseUrl, Class<service> serviceClass){

        if(okHttpClient == null){
            initializeOkHttp();
        }

        if(retrofit_Google == null){
            retrofit_Google = googleBuilder
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(adapterFactory)
                    .build();
        }

        return retrofit_Google.create(serviceClass);
    }

    @NotNull
    public static <service> service getNewYorkTimesClient(String baseUrl, Class<service> serviceClass){

        if(okHttpClient == null){
            initializeOkHttp();
        }

        if(retrofit_NYT == null){
            retrofit_NYT = nytBuilder
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(adapterFactory)
                    .build();
        }

        return retrofit_NYT.create(serviceClass);
    }

    public static void initializeOkHttp(){
        int REQUEST_TIMEOUT = 15;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(interceptor);
        builder.addInterceptor(chain ->  {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Request-Type", "Android")
                    .addHeader("Content-Type", "application/json");

            Request request = requestBuilder.build();

            return chain.proceed(request);
        });

        okHttpClient = builder.build();
    }
}
