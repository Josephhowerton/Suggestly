package com.mortonsworld.suggestly.callbacks;

import com.mortonsworld.suggestly.model.Suggestion;

public interface SuggestionCallback {
    void onSuggestionSelected(Suggestion suggestion);
}
