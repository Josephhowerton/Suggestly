package com.mortonsworld.suggest.source;
import android.app.Application;

import androidx.paging.DataSource;

import com.mortonsworld.suggest.model.nyt.Book;
import com.mortonsworld.suggest.model.nyt.Result;
import com.mortonsworld.suggest.retrofit.NewYorkTimesManager;
import com.mortonsworld.suggest.retrofit.NewYorkTimesService;
import com.mortonsworld.suggest.retrofit.ServiceFactory;
import com.mortonsworld.suggest.room.NewYorkTimesDAO;
import com.mortonsworld.suggest.room.RoomDB;
import com.mortonsworld.suggest.utility.Config;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NewYorkTimesSource {
    private final NewYorkTimesService newYorkTimesService;
    private final NewYorkTimesDAO newYorkTimesDAO;
    private final ExecutorService executorService;
    public NewYorkTimesSource(Application application, ExecutorService service){
        newYorkTimesService = ServiceFactory.getNewYorkTimesClient(Config.NEW_YORK_TIMES_BASE_URL, NewYorkTimesService.class);
        newYorkTimesDAO = RoomDB.getInstance(application).getNewYorkTimesDAO();
        executorService = service;
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

    public void insertNewYorkTimesBookListWithTimeStamp(List<Book> insertBooks, Observer<Void> observer){
        Observable<Void> observable = Observable.create(source -> {
            newYorkTimesDAO.createBooks(insertBooks);
            source.onComplete();
        });
        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void readNewYorkTimesBookUsingISBN(String isbn, Observer<Book> observer){
        newYorkTimesDAO.readBookByISBN(isbn)
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public DataSource.Factory<Integer, Book> readNewYorkTimesBookList(String listName){
        try {
            return executorService.submit(() -> newYorkTimesDAO.readBooksByListName(listName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateNewYorkTimesBook(Book book, Observer<Void> observer){
        Observable<Void> observable = Observable.create(source -> {
            newYorkTimesDAO.updateBook(book);
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void deleteNewYorkTimesBook(Book book, Observer<Void> observer){
        Observable<Void> observable = Observable.create(source -> {
            newYorkTimesDAO.deleteBook(book);
            source.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .subscribe(observer);
    }
}