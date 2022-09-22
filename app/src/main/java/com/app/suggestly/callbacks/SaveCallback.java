package com.app.suggestly.callbacks;

import com.app.suggestly.app.model.Suggestion;

public interface SaveCallback {
    void onSuggestionSaved(Suggestion suggestion, Boolean isChecked);
}
