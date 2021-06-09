package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.model.Suggestion;

public interface SaveCallback {
    void onSuggestionSaved(Suggestion suggestion, Boolean isChecked);
}
