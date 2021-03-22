package com.mortonsworld.suggestly.callbacks;

import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;

public interface SaveCallback {
    void onSuggestionSaved(Suggestion suggestion, int position);
}
