package com.mortonsworld.suggestly.ui.more;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.MoreFragmentAdapter;
import com.mortonsworld.suggestly.databinding.FragmentMoreBinding;
import com.mortonsworld.suggestly.callbacks.DetailsCallback;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MoreFragment extends Fragment implements DetailsCallback {
    private FragmentMoreBinding binding;
    private MoreViewModel moreViewModel;
    private MoreFragmentAdapter adapter;
    private final List<Suggestion> suggestions = new ArrayList<>();
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private static final String TAG = "MoreFragment";
    private boolean hasCalled = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moreViewModel = new ViewModelProvider(this).get(MoreViewModel.class);
        adapter = new MoreFragmentAdapter(requireContext(), suggestions, this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        moreViewModel.initUserLocation().observe(getViewLifecycleOwner(), adapter::setLocationTuple);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        initializeToolbar();
        if(getArguments() != null){
            String id = getArguments().getString(Config.LIST_SUGGESTION_ID_KEY);
            String title = getArguments().getString(Config.LIST_SUGGESTION_TITLE_KEY);
            SuggestionType type = (SuggestionType) getArguments().getSerializable(Config.LIST_SUGGESTION_TYPE_KEY);
            binding.toolbar.setTitle(title);
            switch (type){
                case RECOMMENDED_VENUE:
                    initRecommendedVenues();
                    break;

                case FOURSQUARE_VENUE:
                    initVenueByCategory(id);
                    initOnScrollListener(id);
                    break;

                case BOOK:
                    initBooksByListName(id);
                    break;
            }
        }


        return binding.getRoot();
    }

    public void initRecommendedVenues(){
        moreViewModel.initRecommendedVenues().observe(getViewLifecycleOwner(), data -> {
            initLoadingBar(data.isEmpty());
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    public void initVenueByCategory(String id){
        moreViewModel.initVenues(id).observe(getViewLifecycleOwner(), data -> {
            if (data.size() <= 20 && !hasCalled){
                getUserLocation(id);
                initLoadingBar(!hasCalled);
                return;
            }else{
                initLoadingBar(data.isEmpty());
            }
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    public void initOnScrollListener(String id){
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            getUserLocation(id);
                            loading = true;
                        }
                    }
                }
            }
        });
    }

    public void initBooksByListName(String listName){
        moreViewModel.initBooksByListName(listName).observe(getViewLifecycleOwner(), data -> {
            initLoadingBar(data.isEmpty());
            suggestions.clear();
            suggestions.addAll(data);
            adapter.notifyDataSetChanged();
        });
    }

    private void initializeToolbar(){
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
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

    public void getUserLocation(String id){
        moreViewModel.initUserLocation().observe(getViewLifecycleOwner(), locationTuple -> {
            if(locationTuple != null  && !hasCalled){
                fetchVenues(locationTuple.lat, locationTuple.lng, id);
            }else{
                Toast.makeText(requireContext(), "Couldn't find your location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchVenues(double lat, double lng, String id){
        moreViewModel.fetchVenues(lat, lng, id).observe(getViewLifecycleOwner(), wasSuccess -> {
            if(!wasSuccess){
                Toast.makeText(requireContext(), "Couldn't find venues", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initLoadingBar(boolean loading){
        if(loading){
            binding.suggestLogo.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.suggestLogo.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}