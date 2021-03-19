package com.mortonsworld.suggestly.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.FragmentExploreBinding;
import com.mortonsworld.suggestly.interfaces.CategoryCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.interfaces.SuggestionCallback;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

public class ExploreFragment extends Fragment implements CategoryCallback, SearchView.OnQueryTextListener, SuggestionCallback {
    private FragmentExploreBinding binding;
    private ExploreViewModel exploreViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        exploreViewModel =
                new ViewModelProvider(this).get(ExploreViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        binding.setCategoryCallback(this);
        binding.searchBar.setOnQueryTextListener(this);
        return binding.getRoot();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        exploreViewModel.search(query);
        if(query.isEmpty()){
            binding.recyclerView.setVisibility(View.GONE);
        }else{
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onSuggestionSelected(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onVenueCategoryClickListener(String id, String title) {
        navigateToList(SuggestionType.FOURSQUARE_VENUE, id, title);
    }

    @Override
    public void onBookCategoryClickListener(String list, String title) {
        navigateToList(SuggestionType.BOOK, list, title);
    }

    private void navigateToList(SuggestionType type, String id, String title){
        Bundle bundle = new Bundle();
        bundle.putString(Config.LIST_SUGGESTION_ID_KEY, id);
        bundle.putString(Config.LIST_SUGGESTION_TITLE_KEY, title);
        bundle.putSerializable(Config.LIST_SUGGESTION_TYPE_KEY, type);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_more, bundle);
    }

    private void navigateToDetails(Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, suggestion.getSuggestionType());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    private static final DiffUtil.ItemCallback<Suggestion> VENUE_CALLBACK = new DiffUtil.ItemCallback<Suggestion>() {
        @Override
        public boolean areItemsTheSame(@NonNull Suggestion oldItem, @NonNull Suggestion newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Suggestion oldItem, @NonNull Suggestion newItem) {
            return oldItem.equals(newItem);
        }
    };
}