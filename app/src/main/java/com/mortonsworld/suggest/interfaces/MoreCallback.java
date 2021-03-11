package com.mortonsworld.suggest.interfaces;

import com.mortonsworld.suggest.utility.SuggestionType;

public interface MoreCallback {
    void onMoreRecommended();
    void onMoreSuggestions(SuggestionType type, String id);
}