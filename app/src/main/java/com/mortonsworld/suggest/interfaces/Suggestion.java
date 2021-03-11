package com.mortonsworld.suggest.interfaces;

import androidx.annotation.NonNull;

import com.mortonsworld.suggest.utility.SuggestionType;

public abstract class Suggestion {

    public Suggestion(){}

    @NonNull
    public abstract String getId();

    @NonNull
    public abstract SuggestionType getSuggestionType();

    public Boolean equals(Suggestion suggestion){
        return getId() == suggestion.getId();
    }
}
