package com.josephhowerton.suggestly.app.model.nyt;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.josephhowerton.suggestly.app.model.Suggestion;
import com.josephhowerton.suggestly.utility.SuggestionType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.List;

@Entity
public class Book extends Suggestion {
    @SerializedName("primary_isbn13")
    @PrimaryKey()
    @NonNull
    @Expose
    private String primaryIsbn13;
    @SerializedName("primary_isbn10")
    @Expose
    private String primaryIsbn10;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("rank_last_week")
    @Expose
    private Integer rankLastWeek;
    @SerializedName("weeks_on_list")
    @Expose
    private Integer weeksOnList;
    @SerializedName("asterisk")
    @Expose
    private Integer asterisk;
    @SerializedName("dagger")
    @Expose
    private Integer dagger;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("contributor")
    @Expose
    private String contributor;
    @SerializedName("contributor_note")
    @Expose
    private String contributorNote;
    @SerializedName("book_image")
    @Expose
    private String bookImage;
    @SerializedName("book_image_width")
    @Expose
    private Integer bookImageWidth;
    @SerializedName("book_image_height")
    @Expose
    private Integer bookImageHeight;
    @SerializedName("amazon_product_url")
    @Expose
    private String amazonProductUrl;
    @SerializedName("age_group")
    @Expose
    private String ageGroup;
    @SerializedName("book_review_link")
    @Expose
    private String bookReviewLink;
    @SerializedName("first_chapter_link")
    @Expose
    private String firstChapterLink;
    @SerializedName("sunday_review_link")
    @Expose
    private String sundayReviewLink;
    @SerializedName("article_chapter_link")
    @Expose
    private String articleChapterLink;
    @SerializedName("buy_links")
    @Expose
    private List<BuyLink> buyLinks;
    @SerializedName("book_uri")
    @Expose
    private String bookUri;
    @SerializedName("list_name")
    @Expose
    public String listName;
    @SerializedName("list_name_encoded")
    @Expose
    public String listNameEncoded;
    @SerializedName("bestsellers_date")
    @Expose
    public String bestsellersDate;
    @SerializedName("published_date")
    @Expose
    public String publishedDate;
    @SerializedName("published_date_description")
    @Expose
    public String publishedDateDescription;
    @SerializedName("next_published_date")
    @Expose
    public String nextPublishedDate;
    @SerializedName("previous_published_date")
    @Expose
    public String previousPublishedDate;
    @SerializedName("display_name")
    @Expose
    public String displayName;
    @SerializedName("normal_list_ends_at")
    @Expose
    public Integer normalListEndsAt;
    @SerializedName("updated")
    @Expose
    public String updated;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public Date createdAt;

    public void addExtra(Result result){
        this.listName = result.listName;
        this.listNameEncoded = result.listNameEncoded;
        this.bestsellersDate = result.bestsellersDate;
        this.publishedDate = result.publishedDate;
        this.publishedDateDescription = result.publishedDateDescription;
        this.nextPublishedDate = result.nextPublishedDate;
        this.previousPublishedDate = result.previousPublishedDate;
        this.displayName = result.displayName;
        this.normalListEndsAt = result.normalListEndsAt;
        this.updated = result.updated;
    }

    @Override
    public String getId() {
        return primaryIsbn13;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getRankLastWeek() {
        return rankLastWeek;
    }

    public void setRankLastWeek(Integer rankLastWeek) {
        this.rankLastWeek = rankLastWeek;
    }

    public Integer getWeeksOnList() {
        return weeksOnList;
    }

    public void setWeeksOnList(Integer weeksOnList) {
        this.weeksOnList = weeksOnList;
    }

    public Integer getAsterisk() {
        return asterisk;
    }

    public void setAsterisk(Integer asterisk) {
        this.asterisk = asterisk;
    }

    public Integer getDagger() {
        return dagger;
    }

    public void setDagger(Integer dagger) {
        this.dagger = dagger;
    }

    public String getPrimaryIsbn10() {
        return primaryIsbn10;
    }

    public void setPrimaryIsbn10(String primaryIsbn10) {
        this.primaryIsbn10 = primaryIsbn10;
    }

    public String getPrimaryIsbn13() {
        return primaryIsbn13;
    }

    public void setPrimaryIsbn13(String primaryIsbn13) {
        this.primaryIsbn13 = primaryIsbn13;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTitleWithAuthor(){
        return title + " by " + author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributorNote() {
        return contributorNote;
    }

    public void setContributorNote(String contributorNote) {
        this.contributorNote = contributorNote;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public Integer getBookImageWidth() {
        return bookImageWidth;
    }

    public void setBookImageWidth(Integer bookImageWidth) {
        this.bookImageWidth = bookImageWidth;
    }

    public Integer getBookImageHeight() {
        return bookImageHeight;
    }

    public void setBookImageHeight(Integer bookImageHeight) {
        this.bookImageHeight = bookImageHeight;
    }

    public String getAmazonProductUrl() {
        return amazonProductUrl;
    }

    public void setAmazonProductUrl(String amazonProductUrl) {
        this.amazonProductUrl = amazonProductUrl;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getBookReviewLink() {
        return bookReviewLink;
    }

    public void setBookReviewLink(String bookReviewLink) {
        this.bookReviewLink = bookReviewLink;
    }

    public String getFirstChapterLink() {
        return firstChapterLink;
    }

    public void setFirstChapterLink(String firstChapterLink) {
        this.firstChapterLink = firstChapterLink;
    }

    public String getSundayReviewLink() {
        return sundayReviewLink;
    }

    public void setSundayReviewLink(String sundayReviewLink) {
        this.sundayReviewLink = sundayReviewLink;
    }

    public String getArticleChapterLink() {
        return articleChapterLink;
    }

    public void setArticleChapterLink(String articleChapterLink) {
        this.articleChapterLink = articleChapterLink;
    }

    public List<BuyLink> getBuyLinks() {
        return buyLinks;
    }

    public void setBuyLinks(List<BuyLink> buyLinks) {
        this.buyLinks = buyLinks;
    }

    public String getBookUri() {
        return bookUri;
    }

    public void setBookUri(String bookUri) {
        this.bookUri = bookUri;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListNameEncoded() {
        return listNameEncoded;
    }

    public void setListNameEncoded(String listNameEncoded) {
        this.listNameEncoded = listNameEncoded;
    }

    public String getBestsellersDate() {
        return bestsellersDate;
    }

    public void setBestsellersDate(String bestsellersDate) {
        this.bestsellersDate = bestsellersDate;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublishedDateDescription() {
        return publishedDateDescription;
    }

    public void setPublishedDateDescription(String publishedDateDescription) {
        this.publishedDateDescription = publishedDateDescription;
    }

    public String getNextPublishedDate() {
        return nextPublishedDate;
    }

    public void setNextPublishedDate(String nextPublishedDate) {
        this.nextPublishedDate = nextPublishedDate;
    }

    public String getPreviousPublishedDate() {
        return previousPublishedDate;
    }

    public void setPreviousPublishedDate(String previousPublishedDate) {
        this.previousPublishedDate = previousPublishedDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getNormalListEndsAt() {
        return normalListEndsAt;
    }

    public void setNormalListEndsAt(Integer normalListEndsAt) {
        this.normalListEndsAt = normalListEndsAt;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    @NonNull
    public SuggestionType getSuggestionType() {
        return SuggestionType.BOOK;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
