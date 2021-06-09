package com.josephhowerton.suggestly.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.josephhowerton.suggestly.model.relations.SearchTuple;

@Dao
public interface SearchDao {
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT venue_name, id, category_name, category_icon_prefix, category_icon_suffix FROM Venue JOIN Category ON(venue_category_id = category_id) WHERE venue_name LIKE :search OR category_name LIKE :search LIMIT 75")
    DataSource.Factory<Integer, SearchTuple> venueSearch(String search);
}
