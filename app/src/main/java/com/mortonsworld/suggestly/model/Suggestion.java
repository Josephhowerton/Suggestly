package com.mortonsworld.suggestly.model;

import androidx.annotation.NonNull;

import com.mortonsworld.suggestly.utility.SuggestionType;

public abstract class Suggestion {

    public Suggestion(){}

    @NonNull
    public abstract String getId();

    @NonNull
    public abstract SuggestionType getSuggestionType();

    public Boolean equals(Suggestion suggestion){
        return getId().equals(suggestion.getId());
    }

}
