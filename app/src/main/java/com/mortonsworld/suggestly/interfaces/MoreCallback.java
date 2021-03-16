package com.mortonsworld.suggestly.interfaces;

import com.mortonsworld.suggestly.utility.SuggestionType;

public interface MoreCallback {
    void onMoreRecommended();
    void onMoreSuggestions(SuggestionType type, String id);
}