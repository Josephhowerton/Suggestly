package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

public interface MoreCallback {
    void onMoreSuggestions(@NotNull SuggestionType type, @NotNull String id, @NotNull String title);
}