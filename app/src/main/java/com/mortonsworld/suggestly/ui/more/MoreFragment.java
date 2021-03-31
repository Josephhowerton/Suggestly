package com.mortonsworld.suggestly.ui.more;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.MoreFragmentAdapter;
import com.mortonsworld.suggestly.callbacks.FavoriteCallback;
import com.mortonsworld.suggestly.callbacks.SaveCallback;
import com.mortonsworld.suggestly.databinding.FragmentMoreBinding;
import com.mortonsworld.suggestly.callbacks.DetailsCallback;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.DistanceCalculator;
import com.mortonsworld.suggestly.utility.NetworkHandler;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MoreFragment extends Fragment implements DetailsCallback, SaveCallback, FavoriteCallback {
    private FragmentMoreBinding binding;
    private MoreViewModel moreViewModel;
    private MoreFragmentAdapter adapter;
    private final List<Suggestion> suggestions = new ArrayList<>();
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private boolean hasCalled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moreViewModel = new ViewModelProvider(this).get(MoreViewModel.class);
        adapter = new MoreFragmentAdapter(suggestions, this, this, this);
        adapter.setSavedSuggestions(moreViewModel.saveSuggestions);
        adapter.setFavoriteSuggestions(moreViewModel.favoriteSuggestions);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);


        mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initRecommendedVenues(){
        moreViewModel.initRecommendedVenues().observe(getViewLifecycleOwner(), data -> {
            if(data != null){
                initLoadingBar(data.isEmpty());
                suggestions.clear();
                suggestions.addAll(data);
                adapter.notifyDataSetChanged();
            }
            else{
                new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while reading data from database.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            requireActivity().onBackPressed();
                        }).show();
            }
        });
    }

    public void initVenueByCategory(String id){
        moreViewModel.initVenues(id).observe(getViewLifecycleOwner(), data -> {
            if(data != null){
                checkNetworkConnection(id, data);
                suggestions.clear();
                suggestions.addAll(data);
                adapter.notifyDataSetChanged();
            }
            else{
                new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while reading data from database.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            requireActivity().onBackPressed();
                        }).show();
            }
        });
    }

    public void checkNetworkConnection(String id, List<Suggestion> suggestions){
        if(NetworkHandler.isNetworkConnectionActive(requireActivity())){
            if(suggestions.size() <= 20 && !hasCalled){
                getUserLocation(id);
            }
            binding.suggestLogo.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
        else{
            if(suggestions.isEmpty()){
                String MESSAGE_EMPTY = "You do not have any local Suggestions for this category. Connect to the Internet and try again";
                NetworkHandler.notifyBadConnectionAndGoBack(requireActivity(), MESSAGE_EMPTY);
            }else{
                String MESSAGE_NOT_EMPTY = "In order to provide a greater number of suggestions please connect to the Internet.";
                NetworkHandler.notifyBadConnectionAndDismiss(requireActivity(), MESSAGE_NOT_EMPTY);
                binding.suggestLogo.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public void initOnScrollListener(String id){
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    totalItemCount = mLayoutManager.getItemCount();
                    visibleItemCount = mLayoutManager.getChildCount();
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
            if(data != null){
                initLoadingBar(data.isEmpty());
                suggestions.clear();
                suggestions.addAll(data);
                adapter.notifyDataSetChanged();
            }
            else{
                new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while reading data from database.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            requireActivity().onBackPressed();
                        }).show();
            }
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

    private void navigateToDetails(@NotNull Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
        bundle.putString(Config.DETAILS_SUGGESTION_TITLE_KEY, getSuggestionTitle(suggestion));
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, suggestion.getSuggestionType());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    public void getUserLocation(String id){
        moreViewModel.initUserLocation().observe(getViewLifecycleOwner(), location -> {
            LocationTuple last = moreViewModel.getLastFetchedLocation(location.lat, location.lng);
            if(!hasCalled || DistanceCalculator.distanceMile(location.lat, last.lat, location.lng, last.lng) > 5){
                hasCalled = true;
                storeLastFetchedLocation(location.lat, location.lng);
                fetchVenues(location.lat, location.lng, id);
            }
        });
    }

    public void storeLastFetchedLocation(double lat, double lng){
        moreViewModel.storeLastFetchedLocation(lat, lng);
    }

    public String getSuggestionTitle(@NotNull Suggestion suggestion){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                return ((Book) suggestion).getTitle();
            case FOURSQUARE_VENUE:
                return ((Venue) suggestion).getName();
            default:
                return "Suggestion Details";
        }
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

    @Override
    public void onSuggestionFavorite(Suggestion suggestion, Boolean isFavorite) {
        updateFavoriteSuggestion(suggestion, isFavorite);
    }


    @Override
    public void onSuggestionSaved(Suggestion suggestion, Boolean isChecked) {
        updateSavedSuggestion(suggestion, isChecked);
    }

    private void updateSavedSuggestion(@NotNull Suggestion suggestion, Boolean isSaved){
        switch (suggestion.getSuggestionType()){

            case BOOK:
                moreViewModel.updateBookSavedInUser((Book) suggestion, isSaved);
                break;

            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                moreViewModel.updateVenueSavedInUser(((VenueAndCategory) suggestion).venue, isSaved);
                break;
        }
    }

    public void updateFavoriteSuggestion(Suggestion suggestion, Boolean isFavorite){
        switch (suggestion.getSuggestionType()){

            case BOOK:
                moreViewModel.updateBookFavoriteInUser((Book) suggestion, isFavorite);
                break;

            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                moreViewModel.updateVenueFavoriteInUser(((VenueAndCategory) suggestion).venue, isFavorite);
                break;
        }

    }
}