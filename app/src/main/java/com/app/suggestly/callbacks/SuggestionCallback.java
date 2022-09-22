package com.app.suggestly.callbacks;

import com.app.suggestly.app.model.Suggestion;

public interface SuggestionCallback {
    void onSuggestionSelected(Suggestion suggestion);
}
