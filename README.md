# Suggestly
Suggestly is the first phase of a recommendation app which will serve as the core feature of a larger community.

## Table of Contents
* [General Info](#general-info)
* [Screenshots](#screenshots)
* [Technologies](#technologies)
* [Highlighted Challenge](#highlighted-challenge)

## General Info
Suggestly is a recommendation app used to connect people with things to do. The primary reason was to both build an app I could use and to learn how Android apps are built and maintained from start to finish. Suggestly falls short of my original intentions to establish this virtual utopia instead serving as the initial building blocks. As of today, Suggestly integrates various APIs into a single application connecting users to events, venues, literature and entertainment.

## Screenshots
![Screenshots](https://user-images.githubusercontent.com/58564595/113026881-609f8900-913e-11eb-8dcd-865676acb708.png)


## Technologies
* Android Jetpack
* ReactiveX 3 (Java, Android)
* Firebase Authentication
* Google Places
* Google Cloud
* Foursquare API
* New York Times API
* Retrofit 2
* OkHttp3
* GSON


## Highlighted Challenge
```Two challenges that presented the most difficulty were creating a reactive backend where streams of data pass through dedicated pipelines triggering subsequent behavior. The second challenge was traversing categorical data received from an API response, building transitive closure between related nodes, and inserting into a table. Data is only relevant if it wired back together and presented to the end user. The first function is a recursive function which builds and inserts each node into the transitive closure table, and the second function is an abstract function which brings the data back together.
```java
  public void buildCategoryClosureTable(List<Category> tree, Category target, int depth){
        tree.add(target);

        while (target.hasChildren()){
            Category child = target.removeChild();
            buildCategoryClosureTable(tree, child, depth + 1);
        }

        for(Category parent : tree){
            CategoryClosure categoryClosure = new CategoryClosure(parent.getId(), target.getId(), depth--);
            createCategoryClosureTable(categoryClosure);
        }

        createCategoryBaseTable(target);
        tree.remove(target);
  }

  @Transaction
  @Query("select * from venue v JOIN category c ON(v.venue_category_id = c.category_id) where venue_category_id IN (select child from categoryclosure where parent =:categoryId) ")
  public abstract DataSource.Factory<Integer, VenueAndCategory> readVenueByCategoryId(String categoryId);```
```
