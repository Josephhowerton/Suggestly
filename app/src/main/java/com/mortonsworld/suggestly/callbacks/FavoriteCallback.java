package com.mortonsworld.suggestly.callbacks;

import com.mortonsworld.suggestly.model.Suggestion;

public interface FavoriteCallback {
    void onSuggestionFavorite(Suggestion suggestion, Boolean isFavorite);
}

