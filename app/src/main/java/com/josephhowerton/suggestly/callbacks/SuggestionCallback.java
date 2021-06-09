package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.model.Suggestion;

public interface SuggestionCallback {
    void onSuggestionSelected(Suggestion suggestion);
}
