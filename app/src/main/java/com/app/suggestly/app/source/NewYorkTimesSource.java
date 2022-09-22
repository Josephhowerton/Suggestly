package com.app.suggestly.app.source;
import android.app.Application;

import androidx.paging.DataSource;

import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.app.model.nyt.Result;
import com.app.suggestly.app.network.retrofit.NewYorkTimesManager;
import com.app.suggestly.app.network.retrofit.NewYorkTimesService;
import com.app.suggestly.app.network.retrofit.ServiceFactory;
import com.app.suggestly.app.room.NewYorkTimesDAO;
import com.app.suggestly.app.room.RoomDB;
import com.app.suggestly.utility.Config;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewYorkTimesSource {
    private final NewYorkTimesService newYorkTimesService;
    private final NewYorkTimesDAO newYorkTimesDAO;
    private final ExecutorService executorService;
    public NewYorkTimesSource(Application application){
        newYorkTimesService = ServiceFactory.getNewYorkTimesClient(Config.NEW_YORK_TIMES_BASE_URL, NewYorkTimesService.class);
        newYorkTimesDAO = RoomDB.getInstance(application).getNewYorkTimesDAO();
        executorService = Executors.newFixedThreadPool(5);
    }

    public void isNewYorkTimesTableFresh(Observer<Boolean> observer){
        Observable<Boolean> observable = Observable.create(source -> {
            if(newYorkTimesDAO.isFresh()){
                source.onNext(true);
            } else{
                source.onNext(false);
            }
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void fetchNewYorkTimesBestsellingByListName(String listName, Observer<List<Book>> observer){
        HashMap<String, String> map = NewYorkTimesManager.buildQueryMap();
        newYorkTimesService.fetchBestsellingBooksByListName(listName, map)
                .map(response -> response.results)
                .map(this::convertResultsToBooks)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public List<Book> convertResultsToBooks(Result result){
        for(Book newYorkTimesBook: result.books){
            newYorkTimesBook.addExtra(result);
        }
        return result.books;
    }

    public void readTopSuggestionBook(Observer<Book> observer){
        newYorkTimesDAO.readTopSuggestion()
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void readTopBookUsingISBN13(String isbn13, Observer<Book> observer){
        newYorkTimesDAO.readBookByISBN(isbn13)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void insertNewYorkTimesBookListWithTimeStamp(List<Book> insertBooks, Observer<Void> observer){
        Observable<Void> observable = Observable.create(source -> {
            for(Book book : insertBooks){
                book.createdAt = new Date(System.currentTimeMillis());
            }
            newYorkTimesDAO.createBooks(insertBooks);
            source.onComplete();
        });
        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public DataSource.Factory<Integer, Book> readNewYorkTimesBookListDataFactoryHomeFragment(String listName){
        try {
            return executorService.submit(() -> newYorkTimesDAO.readBooksByListNameDataFactoryHomeFragment(listName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> readBooksByListName(String name){
        try{
            return executorService.submit(() -> newYorkTimesDAO.readBooksByListName(name)).get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Book> readBooksByListNameLimitThree(String isbn13, String name){
        try{
            return executorService.submit(() -> newYorkTimesDAO.readBooksByListNameLimit3(isbn13, name)).get();
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}