package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.app.model.Suggestion;

public interface SuggestionCallback {
    void onSuggestionSelected(Suggestion suggestion);
}
