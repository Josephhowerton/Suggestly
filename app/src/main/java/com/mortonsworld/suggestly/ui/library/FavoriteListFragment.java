package com.mortonsworld.suggestly.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.FavoriteAdapter;
import com.mortonsworld.suggestly.callbacks.FavoriteCallback;
import com.mortonsworld.suggestly.callbacks.SuggestionCallback;
import com.mortonsworld.suggestly.databinding.FragmentFavoritesListBinding;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.utility.Config;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListFragment extends Fragment implements SuggestionCallback, FavoriteCallback, FavoriteAdapter.EmptyListListener {
    public static final String ARG_OBJECT = "Favorites";
    private LibraryViewModel libraryViewModel;
    private final List<Suggestion> suggestions = new ArrayList<>();
    FragmentFavoritesListBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites_list, container, false);
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(suggestions, this, this, this);
        binding.recyclerView.setAdapter(favoriteAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));

        libraryViewModel.readFavoriteSuggestions().observe(getViewLifecycleOwner(), favoriteSuggestions -> {
            if(!favoriteSuggestions.isEmpty()){
                suggestions.clear();
                suggestions.addAll(favoriteSuggestions);
                favoriteAdapter.notifyDataSetChanged();
            }else{
                onEmptyList();
            }
            binding.loadingLayout.loadingLayout.setVisibility(View.GONE);
        });

        binding.addToFavoritesButton.setOnClickListener(view -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_search));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSuggestionSelected(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onSuggestionFavorite(Suggestion suggestion, Boolean isChecked) {
        libraryViewModel.deleteSavedSuggestion(suggestion);
    }

    @Override
    public void onEmptyList() {
        binding.addToFavoritesButton.setVisibility(View.VISIBLE);
        binding.addFavoriteImage.setVisibility(View.VISIBLE);
        binding.addToFavoritesMessage.setVisibility(View.VISIBLE);
    }

    private void navigateToDetails(Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
        bundle.putString(Config.DETAILS_SUGGESTION_TITLE_KEY, getSuggestionTitle(suggestion));
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, suggestion.getSuggestionType());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    public String getSuggestionTitle(Suggestion suggestion){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                return ((Book) suggestion).getTitle();
            case FOURSQUARE_VENUE:
                return ((Venue) suggestion).getName();
            default:
                return "Suggestion Details";
        }
    }


}