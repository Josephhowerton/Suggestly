package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.app.model.Suggestion;

public interface FavoriteCallback {
    void onSuggestionFavorite(Suggestion suggestion, Boolean isFavorite);
}

