package com.mortonsworld.suggest.ui.home;

import android.content.Intent;
import android.os.Bundle;
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
import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.adapter.HomeBookAdapter;
import com.mortonsworld.suggest.adapter.HomeVenueAdapter;
import com.mortonsworld.suggest.databinding.FragmentHomeBinding;
import com.mortonsworld.suggest.interfaces.MoreCallback;
import com.mortonsworld.suggest.interfaces.Suggestion;
import com.mortonsworld.suggest.interfaces.TopSuggestionCallback;
import com.mortonsworld.suggest.model.foursquare.FoursquareVenue;
import com.mortonsworld.suggest.model.nyt.Book;
import com.mortonsworld.suggest.ui.main.MainViewModel;
import com.mortonsworld.suggest.ui.settings.SettingsActivity;
import com.mortonsworld.suggest.utility.Config;
import com.mortonsworld.suggest.utility.SuggestionType;

public class HomeFragment extends Fragment implements MoreCallback, TopSuggestionCallback, HomeVenueAdapter.VenueSelectedListener,
        HomeBookAdapter.BookSelectedListener {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private MainViewModel mainViewModel;

    private final HomeVenueAdapter recommendedAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeVenueAdapter foodAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeVenueAdapter breweryAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeBookAdapter fictionAdapter = new HomeBookAdapter(BOOK_CALLBACK, this);
    private final HomeVenueAdapter familyAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeBookAdapter nonFictionAdapter = new HomeBookAdapter(BOOK_CALLBACK, this);
    private final HomeVenueAdapter activeAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeVenueAdapter socialAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);
    private final HomeVenueAdapter entertainmentAdapter = new HomeVenueAdapter(VENUE_CALLBACK, this);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

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
        binding.setTop(this);

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
    public void onMoreRecommended() {
        navigateToList(SuggestionType.FOURSQUARE_VENUE, Config.LIST_RECOMMENDED_ID_KEY);
    }

    @Override
    public void onMoreSuggestions(SuggestionType type, String id) {
        navigateToList(type, id);
    }

    @Override
    public void onTopSuggestionListener(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onBookSelected(Book book) {
        mainViewModel.fetchSuggestionBookDetails(book.getPrimaryIsbn13());
        navigateToDetails(book);
    }

    @Override
    public void onVenueSelected(FoursquareVenue venue) {
        mainViewModel.fetchSuggestionVenueDetails(venue.getId());
        navigateToDetails(venue);
    }

    private void navigateToList(SuggestionType type, String id){
        Bundle bundle = new Bundle();
        bundle.putString(Config.LIST_SUGGESTION_ID_KEY, id);
        bundle.putSerializable(Config.LIST_SUGGESTION_TYPE_KEY, type);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    private void navigateToDetails(Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
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
        binding.suggestionsBreweryRecyclerView.setAdapter(nonFictionAdapter);
        binding.suggestionsFamilyFunRecyclerView.setAdapter(fictionAdapter);
        binding.suggestionsNytNonfictionRecyclerView.setAdapter(nonFictionAdapter);
        binding.suggestionsActiveRecyclerView.setAdapter(fictionAdapter);
        binding.suggestionsSocialRecyclerView.setAdapter(nonFictionAdapter);
        binding.suggestionsRecommendedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsRestaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytFictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsBreweryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsFamilyFunRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytNonfictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsActiveRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsSocialRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
    }


    private void initializeRecommendedVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), recommendedAdapter::submitList);
    }

    private void initializeFoodVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), recommendedAdapter::submitList);
    }

    private void initializeFictionBooks(){
        homeViewModel.fictionBooksPagedList.observe(getViewLifecycleOwner(), fictionAdapter::submitList);
    }


    private void initializeBreweryVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), recommendedAdapter::submitList);
    }

    private void initializeFamilyFunVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), recommendedAdapter::submitList);
    }

    private void initializeNonFictionBooks(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), nonFictionAdapter::submitList);
    }

    private void initializeActiveVenues(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), nonFictionAdapter::submitList);
    }

    private void initializeSocialVenues(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), nonFictionAdapter::submitList);
    }

    private void initializeEntertainmentVenues(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), nonFictionAdapter::submitList);
    }

    public void goToSettingsActivity(){
        Intent intent = new Intent(requireActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private static final DiffUtil.ItemCallback<FoursquareVenue> VENUE_CALLBACK = new DiffUtil.ItemCallback<FoursquareVenue>() {
        @Override
        public boolean areItemsTheSame(@NonNull FoursquareVenue oldItem, @NonNull FoursquareVenue newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FoursquareVenue oldItem, @NonNull FoursquareVenue newItem) {
            return oldItem.equals(newItem);
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