package com.mortonsworld.suggest.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mortonsworld.suggest.model.foursquare.CategoryWithVenues;
import com.mortonsworld.suggest.model.foursquare.FoursquareCategory;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class FoursquareCategoryDao {

    @Transaction
    public Boolean isTableEmpty() {
        return checkIfTableIsEmpty() == null;
    }

    @Transaction
    public void createClosureTable(FoursquareCategory category, Integer depth) {
        createCategory(category);
        updateCategoryDepth(category.getCategoryId(), depth);
    }

    @Transaction
    public List<FoursquareVenue> readAllVenuesWithCategory(String id){
        List<FoursquareVenue> foursquareVenues = new ArrayList<>();
        for(FoursquareCategory category : readAllCategoriesWithParentId(id)){
            List<CategoryWithVenues> temp = readAllVenuesWithCategoryId(category.categoryId);
            for(CategoryWithVenues crossRef : temp){
                foursquareVenues.addAll(crossRef.venues);
            }
        }
        return foursquareVenues;
    }

    @Query("SELECT * FROM FoursquareCategory LIMIT 1")
    protected abstract FoursquareCategory checkIfTableIsEmpty();

    @Insert
    public abstract void createCategory(FoursquareCategory category);

    @Query("SELECT * FROM foursquarecategory where category_id = :id")
    public abstract List<FoursquareCategory> readAllCategoriesWithParentId(String id);

    @Transaction
    @Query("SELECT * FROM foursquarecategory where category_id = :id ORDER BY RANDOM() LIMIT 10")
    public abstract List<CategoryWithVenues> readAllVenuesWithCategoryId(String id);

    @Transaction
    @Query("SELECT * FROM foursquarecategory where category_id = :id ORDER BY RANDOM()")
    public abstract DataSource.Factory<Integer, CategoryWithVenues> readAllVenuesWithCategoryIdDataFactory(String id);

    @Query("UPDATE foursquarecategory SET depth =:depth WHERE category_id=:id")
    public abstract int updateCategoryDepth(String id, Integer depth);
}
