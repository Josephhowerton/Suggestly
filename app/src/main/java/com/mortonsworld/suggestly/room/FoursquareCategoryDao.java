package com.mortonsworld.suggestly.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.CategoryClosure;

import java.util.List;

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

    @Query("SELECT * FROM CategoryClosure WHERE parent = :id")
    public abstract List<CategoryClosure> readAllCategoriesWithParentId(String id);

    @Query("SELECT * FROM CategoryClosure JOIN category c on(child == category_id) WHERE parent = (select parent from categoryclosure where child=:id AND depth=1)")
    public abstract DataSource.Factory<Integer, Category> readRelatedCategories(String id);

    @Query("SELECT * FROM Category WHERE category_id = :id")
    public abstract LiveData<Category> readFoursquareCategory(String id);
}
