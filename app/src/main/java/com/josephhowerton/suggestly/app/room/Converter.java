package com.josephhowerton.suggestly.app.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.josephhowerton.suggestly.app.model.nyt.Book;
import com.josephhowerton.suggestly.app.model.nyt.BuyLink;

import java.sql.Date;
import java.util.List;

public class Converter {
    @TypeConverter
    public static Date toDate(String date){
        return date == null ? null : Date.valueOf(date);
    }

    @TypeConverter
    public static String fromDate(Date date){
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static List<BuyLink> toBuyLinks(String links){
        return links == null ? null : new Gson().fromJson(links, new TypeToken<List<BuyLink>>(){}.getType());
    }

    @TypeConverter
    public static String fromDate(List<BuyLink> links){
        return links == null ? null : new Gson().toJson(links, new TypeToken<List<BuyLink>>(){}.getType());
    }

    @TypeConverter
    public static List<Book>  toSuggestionList(String suggestions){
        return suggestions == null ? null : new Gson().fromJson(suggestions, new TypeToken<List<Book> >(){}.getType());
    }

    @TypeConverter
    public static String fromSuggestionList(List<Book> suggestions){
        return suggestions == null ? null : new Gson().toJson(suggestions, new TypeToken<List<Book> >(){}.getType());
    }
}
