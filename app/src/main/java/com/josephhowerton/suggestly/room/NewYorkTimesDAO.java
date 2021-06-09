package com.josephhowerton.suggestly.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.josephhowerton.suggestly.model.nyt.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;

@Dao
public abstract class NewYorkTimesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createBooks(List<Book> books);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createBook(Book book);

    @Query("SELECT * FROM Book ORDER BY publishedDate DESC LIMIT 1")
    public abstract Book readFreshestEntry();

    @Query("SELECT * FROM Book ORDER BY RANDOM() LIMIT 1")
    public abstract Observable<Book> readTopSuggestion();

    @Query("SELECT * FROM Book WHERE primaryIsbn13 = :isbn13")
    public abstract Observable<Book> readBookByISBN(String isbn13);

    @Query("SELECT * FROM Book WHERE listNameEncoded =:name")
    public abstract DataSource.Factory<Integer, Book> readBooksByListNameDataFactory(String name);

    @Query("SELECT * FROM Book WHERE listNameEncoded =:name LIMIT 10")
    public abstract DataSource.Factory<Integer, Book> readBooksByListNameDataFactoryHomeFragment(String name);

    @Query("SELECT * FROM Book WHERE listNameEncoded =:name")
    public abstract List<Book> readBooksByListName(String name);

    @Query("SELECT * FROM Book WHERE (listNameEncoded=:name AND primaryIsbn13 !=:isbn13 AND RANDOM()) LIMIT 3")
    public abstract List<Book> readBooksByListNameLimit3(String isbn13, String name);

    @Query("SELECT * FROM Book")
    public abstract Observable<List<Book>> readAllBooks();

    @Update
    public abstract void updateBook(Book book);

    @Delete
    public abstract void deleteBook(Book book);

    @Transaction
    public Boolean isEmpty(Book book){
        return book == null;
    }

    @Transaction
    public Boolean isFresh(){
        Book book = readFreshestEntry();
        if(isEmpty(book)){
            return false;
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = simpleDateFormat.parse(book.getPublishedDate());
            boolean b = new Date().after(date);
            if(b){
                return false;
            }
        }catch (ParseException e){
            return false;
        }

        return true;
    }
}