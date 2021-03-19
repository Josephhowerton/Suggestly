package com.mortonsworld.suggestly.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.HomeBookAdapter;
import com.mortonsworld.suggestly.adapter.HomeVenueAdapter;
import com.mortonsworld.suggestly.adapter.MoreFragmentAdapter;
import com.mortonsworld.suggestly.databinding.FragmentHomeBinding;
import com.mortonsworld.suggestly.interfaces.DetailsCallback;
import com.mortonsworld.suggestly.interfaces.MoreCallback;
import com.mortonsworld.suggestly.interfaces.SaveCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.ui.main.MainViewModel;
import com.mortonsworld.suggestly.ui.settings.SettingsActivity;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

public class HomeFragment extends Fragment implements MoreCallback, DetailsCallback, HomeVenueAdapter.VenueSelectedListener,
        HomeBookAdapter.BookSelectedListener, SaveCallback {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private MainViewModel mainViewModel;

    private final HomeVenueAdapter recommendedAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeVenueAdapter foodAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeVenueAdapter breweryAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeBookAdapter fictionAdapter = new HomeBookAdapter(BOOK_CALLBACK, this, this);
    private final HomeVenueAdapter familyAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeBookAdapter nonFictionAdapter = new HomeBookAdapter(BOOK_CALLBACK, this, this);
    private final HomeVenueAdapter activeAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeVenueAdapter socialAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);
    private final HomeVenueAdapter entertainmentAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this, this);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        homeViewModel.locationTupleLiveData.observe(getViewLifecycleOwner(), locationTuple -> {
            recommendedAdapter.setLocationTuple(locationTuple);
            foodAdapter.setLocationTuple(locationTuple);
            breweryAdapter.setLocationTuple(locationTuple);
            familyAdapter.setLocationTuple(locationTuple);
            activeAdapter.setLocationTuple(locationTuple);
            socialAdapter.setLocationTuple(locationTuple);
            entertainmentAdapter.setLocationTuple(locationTuple);
        });

        initializeTopSuggestion();
        initializeRecommendedVenues();
        initializeFoodVenues();
        initializeFictionBooks();
        initializeBreweryVenues();
        initializeFamilyFunVenues();
        initializeNonFictionBooks();
        initializeActiveVenues();
        initializeSocialVenues();
        initializeEntertainmentVenues();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container,  false);
        setHasOptionsMenu(true);
        binding.setMore(this);
        binding.setDetails(this);

        initializeRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            goToSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoreSuggestions(SuggestionType type, String id, String title) {
        navigateToList(type, id, title);
    }

    @Override
    public void onSuggestionDetailsListener(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onBookSelected(Book book) {
        mainViewModel.fetchSuggestionBookDetails(book.getPrimaryIsbn13());
        navigateToDetails(book);
    }

    @Override
    public void onVenueSelected(Venue venue) {
        mainViewModel.fetchSuggestionVenueDetails(venue.getId());
        navigateToDetails(venue);
    }

    @Override
    public void onSuggestionSaved() {
        Log.println(Log.ASSERT, "Saved", "saving");
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

    private void initializeTopSuggestion(){
        homeViewModel.topSuggestion.observe(getViewLifecycleOwner(), book -> {
            binding.titleTopSuggestionTitle.setText(book.getTitleWithAuthor());
            binding.titleTopSuggestionDescription.setText(book.getDescription());
            binding.setSuggestion(book);
            Glide.with(this)
                    .load(book.getBookImage())
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.imageTopSuggestion);
        });
    }

    private void initializeRecyclerView(){
        binding.suggestionsRecommendedRecyclerView.setAdapter(recommendedAdapter);
        binding.suggestionsRestaurantsRecyclerView.setAdapter(foodAdapter);
        binding.suggestionsNytFictionRecyclerView.setAdapter(fictionAdapter);
        binding.suggestionsBreweryRecyclerView.setAdapter(breweryAdapter);
        binding.suggestionsFamilyFunRecyclerView.setAdapter(familyAdapter);
        binding.suggestionsNytNonfictionRecyclerView.setAdapter(nonFictionAdapter);
        binding.suggestionsActiveRecyclerView.setAdapter(activeAdapter);
        binding.suggestionsSocialRecyclerView.setAdapter(socialAdapter);
        binding.suggestionsEntertainmentRecyclerView.setAdapter(entertainmentAdapter);
        binding.suggestionsRecommendedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsRestaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytFictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsBreweryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsFamilyFunRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytNonfictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsActiveRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsSocialRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsEntertainmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
    }


    private void initializeRecommendedVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), recommendedAdapter::submitList);
    }

    private void initializeFoodVenues(){
        homeViewModel.foodVenuePagedList.observe(getViewLifecycleOwner(), foodAdapter::submitList);
    }

    private void initializeFictionBooks(){
        homeViewModel.fictionBooksPagedList.observe(getViewLifecycleOwner(), fictionAdapter::submitList);
    }


    private void initializeBreweryVenues(){
        homeViewModel.breweryVenuePagedList.observe(getViewLifecycleOwner(), breweryAdapter::submitList);
    }

    private void initializeFamilyFunVenues(){
        homeViewModel.familyVenuePagedList.observe(getViewLifecycleOwner(), familyAdapter::submitList);
    }

    private void initializeNonFictionBooks(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), nonFictionAdapter::submitList);
    }

    private void initializeActiveVenues(){
        homeViewModel.activeVenuePagedList.observe(getViewLifecycleOwner(), activeAdapter::submitList);
    }

    private void initializeSocialVenues(){
        homeViewModel.socialVenuePagedList.observe(getViewLifecycleOwner(), socialAdapter::submitList);
    }

    private void initializeEntertainmentVenues(){
        homeViewModel.entertainmentVenuePagedList.observe(getViewLifecycleOwner(), entertainmentAdapter::submitList);
    }

    public void goToSettingsActivity(){
        Intent intent = new Intent(requireActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private static final DiffUtil.ItemCallback<VenueAndCategory> VENUE_CALLBACK = new DiffUtil.ItemCallback<VenueAndCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull VenueAndCategory oldItem, @NonNull VenueAndCategory newItem) {
            return oldItem.venue.getId().equals(newItem.venue.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull VenueAndCategory oldItem, @NonNull VenueAndCategory newItem) {
            return oldItem.venue.equals(newItem.venue);
        }
    };

    private static final DiffUtil.ItemCallback<Book> BOOK_CALLBACK = new DiffUtil.ItemCallback<Book>() {
        @Override
        public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
            return oldItem.equals(newItem);
        }
    };
}