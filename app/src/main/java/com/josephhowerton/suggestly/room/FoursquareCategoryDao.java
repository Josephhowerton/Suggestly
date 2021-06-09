package com.josephhowerton.suggestly.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.josephhowerton.suggestly.model.foursquare.Category;
import com.josephhowerton.suggestly.model.relations.CategoryClosure;
import com.josephhowerton.suggestly.model.relations.CategoryTuple;

@Dao
public abstract class FoursquareCategoryDao {

    @Query("SELECT * FROM Category LIMIT 1")
    protected abstract Category checkIfTableIsEmpty();

    @Transaction
    public Boolean isTableEmpty() {
        return checkIfTableIsEmpty() == null;
    }

    @Insert
    public abstract void createBaseTable(Category category);

    @Insert
    public abstract void createClosureTable(CategoryClosure categoryClosure);

    @Query("SELECT DISTINCT category_id, category_name, category_icon_prefix, category_icon_suffix FROM " +
            "CategoryClosure JOIN category c on(child == category_id) WHERE parent IN (select parent " +
            "FROM categoryclosure WHERE child=:id) LIMIT 5 ")
    public abstract DataSource.Factory<Integer, CategoryTuple> readRelatedCategories(String id);

    @Query("SELECT * FROM Category WHERE category_id = :id")
    public abstract LiveData<Category> readFoursquareCategory(String id);
}
