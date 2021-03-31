package com.mortonsworld.suggestly.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.SuggestlySearchAdapter;
import com.mortonsworld.suggestly.databinding.FragmentExploreBinding;
import com.mortonsworld.suggestly.callbacks.CategoryCallback;
import com.mortonsworld.suggestly.model.relations.SearchTuple;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.NetworkHandler;
import com.mortonsworld.suggestly.utility.SuggestionType;

public class ExploreFragment extends Fragment implements CategoryCallback, SearchView.OnQueryTextListener, SuggestlySearchAdapter.SearchResultListener {
    private FragmentExploreBinding binding;
    private ExploreViewModel exploreViewModel;
    private final SuggestlySearchAdapter searchAdapter = new SuggestlySearchAdapter(this);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);

        binding.searchLayout.recyclerView.setAdapter(searchAdapter);
        binding.searchLayout.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.setCategoryCallback(this);
        binding.searchBar.setOnQueryTextListener(this);

        initializeSuggestlySearch();

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!NetworkHandler.isNetworkConnectionActive(requireActivity())){
            Toast.makeText(requireContext(), R.string.message_network_error_search, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if(query.isEmpty()){
            binding.searchLayout.getRoot().setVisibility(View.GONE);
            binding.searchLayout.recyclerView.setVisibility(View.GONE);
        }else{
            suggestlySearch(query);
            binding.searchLayout.getRoot().setVisibility(View.VISIBLE);
            binding.searchLayout.recyclerView.setVisibility(View.VISIBLE);
        }
        binding.searchLayout.message.setVisibility(View.GONE);
        return true;
    }

    public void suggestlySearch(String search){
        exploreViewModel.suggestlySearch(search).observe(getViewLifecycleOwner(), data ->{

            if(data.size() == 0){
                binding.searchLayout.recyclerView.setVisibility(View.GONE);
                binding.searchLayout.message.setVisibility(View.VISIBLE);
            }

            searchAdapter.submitList(data);
        });
    }

    @Override
    public void onResultSelected(SearchTuple searchResult) {
        navigateToDetails(searchResult);
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

    private void navigateToDetails(SearchTuple searchResult){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, searchResult.venueId);
        bundle.putString(Config.LIST_SUGGESTION_TITLE_KEY, searchResult.name);
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, SuggestionType.FOURSQUARE_VENUE);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    private void initializeSuggestlySearch(){
        exploreViewModel.initializeVenueSearch();
    }

}