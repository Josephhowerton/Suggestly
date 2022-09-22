package com.app.suggestly.callbacks;

import com.app.suggestly.app.model.Suggestion;

public interface FavoriteCallback {
    void onSuggestionFavorite(Suggestion suggestion, Boolean isFavorite);
}

