package com.app.suggestly.callbacks;

import com.app.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

public interface MoreCallback {
    void onMoreSuggestions(@NotNull SuggestionType type, @NotNull String id, @NotNull String title);
}