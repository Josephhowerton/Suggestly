package com.josephhowerton.suggestly.callbacks;

import com.josephhowerton.suggestly.app.model.Suggestion;

public interface SaveCallback {
    void onSuggestionSaved(Suggestion suggestion, Boolean isChecked);
}
