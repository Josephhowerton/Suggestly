package com.mortonsworld.suggestly.ui.more;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.MoreFragmentAdapter;
import com.mortonsworld.suggestly.databinding.FragmentMoreBinding;
import com.mortonsworld.suggestly.interfaces.DetailsCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MoreFragment extends Fragment implements DetailsCallback {
    private MoreViewModel moreViewModel;
    private MoreFragmentAdapter adapter;
    private final List<Suggestion> suggestions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moreViewModel = new ViewModelProvider(this).get(MoreViewModel.class);
        adapter = new MoreFragmentAdapter(requireContext(), suggestions, this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        moreViewModel.initUserLocation().observe(getViewLifecycleOwner(), adapter::setLocationTuple);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        if(getArguments() != null){
            String id = getArguments().getString(Config.LIST_SUGGESTION_ID_KEY);
            String title = getArguments().getString(Config.LIST_SUGGESTION_TITLE_KEY);
            SuggestionType type = (SuggestionType) getArguments().getSerializable(Config.LIST_SUGGESTION_TYPE_KEY);
            switch (type){
                case RECOMMENDED_VENUE:
                    initTitle(title);
                    initRecommendedVenues();
                    break;

                case FOURSQUARE_VENUE:
                    initTitle(title);
                    initVenueByCategory(id);
                    break;

                case BOOK:
                    initTitle(title);
                    initBooksByListName(id);
                    break;
            }
        }

        return binding.getRoot();
    }

    public void initRecommendedVenues(){
        moreViewModel.initRecommendedVenues().observe(getViewLifecycleOwner(), data -> {
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    public void initVenueByCategory(String id){
        moreViewModel.initVenues(id).observe(getViewLifecycleOwner(), data -> {
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    public void initBooksByListName(String listName){
        moreViewModel.initBooksByListName(listName).observe(getViewLifecycleOwner(), data -> {
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    public void initTitle(String title){
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onSuggestionDetailsListener(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    private void navigateToDetails(Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }
}