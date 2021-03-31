package com.mortonsworld.suggestly.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.HomeBookAdapter;
import com.mortonsworld.suggestly.adapter.HomeVenueAdapter;
import com.mortonsworld.suggestly.callbacks.DetailsCallback;
import com.mortonsworld.suggestly.callbacks.MoreCallback;
import com.mortonsworld.suggestly.callbacks.SaveCallback;
import com.mortonsworld.suggestly.databinding.FragmentHomeBinding;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.ui.main.MainActivity;
import com.mortonsworld.suggestly.ui.settings.SettingsActivity;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.DistanceCalculator;
import com.mortonsworld.suggestly.utility.NetworkHandler;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment implements MoreCallback, DetailsCallback, HomeVenueAdapter.VenueSelectedListener,
        HomeBookAdapter.BookSelectedListener, SaveCallback, MainActivity.OnReloadListener {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private final HomeVenueAdapter recommendedAdapter = new HomeVenueAdapter(this, this);
    private final HomeVenueAdapter foodAdapter = new HomeVenueAdapter(this, this);
    private final HomeVenueAdapter breweryAdapter = new HomeVenueAdapter(this, this);
    private final HomeBookAdapter fictionAdapter = new HomeBookAdapter(BOOK_CALLBACK,this, this);
    private final HomeVenueAdapter familyAdapter = new HomeVenueAdapter(this, this);
    private final HomeBookAdapter nonFictionAdapter = new HomeBookAdapter(BOOK_CALLBACK, this, this);
    private final HomeVenueAdapter activeAdapter = new HomeVenueAdapter(this, this);
    private final HomeVenueAdapter socialAdapter = new HomeVenueAdapter(this, this);
    private final HomeVenueAdapter entertainmentAdapter = new HomeVenueAdapter(this, this);
    private final String MESSAGE = "To find Suggestions in your area connect to the Internet.";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        initializeSavedList();
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

        homeViewModel.getUserLocation().observe(getViewLifecycleOwner(), location -> {
            LocationTuple last = homeViewModel.getLastFetchedLocation(location.lat, location.lng);
            if(DistanceCalculator.distanceMile(location.lat, last.lat, location.lng, last.lng) > 2){
                storeLastFetchedLocation(location.lat, location.lng);
                if(NetworkHandler.isNetworkConnectionActive(requireActivity())){
                    fetchVenues(location);
                }else{
                    NetworkHandler.notifyBadConnectionAndDismiss(requireActivity(), MESSAGE);
                }
            }
        });

        ((MainActivity) requireActivity()).setOnReloadClickListener(this);
    }

    public void storeLastFetchedLocation(double lat, double lng){
        homeViewModel.storeLastFetchedLocation(lat, lng);
    }

    public void initializeSavedList(){
        fictionAdapter.setSavedList(homeViewModel.getSavedBooks());
        nonFictionAdapter.setSavedList(homeViewModel.getSavedBooks());
        recommendedAdapter.setSavedList(homeViewModel.getSavedVenues());
        foodAdapter.setSavedList(homeViewModel.getSavedVenues());
        breweryAdapter.setSavedList(homeViewModel.getSavedVenues());
        familyAdapter.setSavedList(homeViewModel.getSavedVenues());
        activeAdapter.setSavedList(homeViewModel.getSavedVenues());
        socialAdapter.setSavedList(homeViewModel.getSavedVenues());
        entertainmentAdapter.setSavedList(homeViewModel.getSavedVenues());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container,  false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
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
    public void onMoreSuggestions(@NonNull SuggestionType type, @NonNull String id, @NonNull String title) {
        navigateToList(type, id, title);
    }

    @Override
    public void onSuggestionDetailsListener(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onBookSelected(Book book) {
        navigateToDetails(book);
    }

    @Override
    public void onVenueSelected(Venue venue) {
        navigateToDetails(venue);
    }

    private boolean isRECOMMENDEDCompleted = false;
    private boolean isFOODCompleted = false;
    private boolean isBREWERYCompleted = false;
    private boolean isFAMILYFUNCompleted = false;
    private boolean isEVENTSCompleted = false;
    private boolean isACTIVECompleted = false;
    private boolean isSOCIALCompleted = false;

    public void fetchVenues(LocationTuple locationTuple) {
        Toast.makeText(requireContext(), "Finding new suggestions near you!", Toast.LENGTH_SHORT).show();
        getFoursquareVenuesNearUser_RECOMMENDED(locationTuple);
        getFoursquareVenuesNearUser_FOOD(locationTuple);
        getFoursquareVenuesNearUser_BREWERY(locationTuple);
        getFoursquareVenuesNearUser_FAMILY_FUN(locationTuple);
        getFoursquareVenuesNearUser_EVENTS(locationTuple);
        getFoursquareVenuesNearUser_ACTIVE(locationTuple);
        getFoursquareVenuesNearUser_SOCIAL(locationTuple);
    }

    public void getFoursquareVenuesNearUser_RECOMMENDED(LocationTuple locationTuple){
        homeViewModel.getRecommendedFoursquareVenuesNearUser(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isRECOMMENDEDCompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_FOOD(LocationTuple locationTuple){
        homeViewModel.getFoursquareVenuesNearUser_FOOD(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isFOODCompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_BREWERY(LocationTuple locationTuple){
        homeViewModel.getGeneralFoursquareVenuesNearUser_BREWERY(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isBREWERYCompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_FAMILY_FUN(LocationTuple locationTuple){
        homeViewModel.getGeneralFoursquareVenuesNearUserById_FAMILY_FUN(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isFAMILYFUNCompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_EVENTS(LocationTuple locationTuple){
        homeViewModel.getGeneralFoursquareVenuesNearUserById_EVENTS(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isEVENTSCompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_ACTIVE(LocationTuple locationTuple){
        homeViewModel.getGeneralFoursquareVenuesNearUserById_ACTIVE(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isACTIVECompleted = true;
            checkUpdateComplete();
        });
    }

    public void getFoursquareVenuesNearUser_SOCIAL(LocationTuple locationTuple){
        homeViewModel.getGeneralFoursquareVenuesNearUserById_SOCIAL(locationTuple.lat, locationTuple.lng).observe(requireActivity(), aBoolean -> {
            isSOCIALCompleted = true;
            checkUpdateComplete();
        });
    }

    public void checkUpdateComplete(){
        if(isRECOMMENDEDCompleted && isFOODCompleted && isBREWERYCompleted && isFAMILYFUNCompleted &&
                isEVENTSCompleted && isACTIVECompleted && isSOCIALCompleted){
            isRECOMMENDEDCompleted = false;
            isFOODCompleted = false;
            isBREWERYCompleted = false;
            isFAMILYFUNCompleted = false;
            isEVENTSCompleted = false;
            isACTIVECompleted = false;
            isSOCIALCompleted = false;
        }

    }

    @Override
    public void onSuggestionSaved(Suggestion suggestion, Boolean isChecked) {
        updateSavedSuggestion(suggestion, isChecked);
    }

    private void updateSavedSuggestion(@NotNull Suggestion suggestion, Boolean isSaved){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                homeViewModel.updateBookSavedInUser((Book) suggestion, isSaved);
                break;

            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                homeViewModel.updateVenueSavedInUser((Venue) suggestion, isSaved);
                break;
        }
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
        bundle.putString(Config.DETAILS_SUGGESTION_TITLE_KEY, getSuggestionTitle(suggestion));
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, suggestion.getSuggestionType());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    private void initializeTopSuggestion(){
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(requireContext());
        circularProgressDrawable.start();
        homeViewModel.topSuggestion.observe(getViewLifecycleOwner(), book -> {
            binding.titleMainTitle.setText(book.getTitleWithAuthor());
            binding.titleTopSuggestionDescription.setText(book.getDescription());
            binding.setSuggestion(book);
            Glide.with(this)
                    .load(book.getBookImage())
                    .placeholder(circularProgressDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageTopSuggestion);
        });
    }

    private void initializeRecyclerView(){
        binding.suggestionsRecommendedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsRestaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytFictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsBreweryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsFamilyFunRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsNytNonfictionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsActiveRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsSocialRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        binding.suggestionsEntertainmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));

        binding.suggestionsRecommendedRecyclerView.setAdapter(recommendedAdapter);
        binding.suggestionsRestaurantsRecyclerView.setAdapter(foodAdapter);
        binding.suggestionsNytFictionRecyclerView.setAdapter(fictionAdapter);
        binding.suggestionsBreweryRecyclerView.setAdapter(breweryAdapter);
        binding.suggestionsFamilyFunRecyclerView.setAdapter(familyAdapter);
        binding.suggestionsNytNonfictionRecyclerView.setAdapter(nonFictionAdapter);
        binding.suggestionsActiveRecyclerView.setAdapter(activeAdapter);
        binding.suggestionsSocialRecyclerView.setAdapter(socialAdapter);
        binding.suggestionsEntertainmentRecyclerView.setAdapter(entertainmentAdapter);
    }

    private boolean hasShown = false;

    private void initializeRecommendedVenues(){
        homeViewModel.recommendedVenuePagedList.observe(getViewLifecycleOwner(), data ->{
            if(data != null){
                recommendedAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeFoodVenues(){
        homeViewModel.foodVenuePagedList.observe(getViewLifecycleOwner(), data -> {
                if (data != null) {
                    foodAdapter.submitList(data);
                }else{
                    alertDatabaseError();
                }
            }
        );
    }

    private void initializeFictionBooks(){
        homeViewModel.fictionBooksPagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                fictionAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }


    private void initializeBreweryVenues(){
        homeViewModel.breweryVenuePagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                breweryAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeFamilyFunVenues(){
        homeViewModel.familyVenuePagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                familyAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeNonFictionBooks(){
        homeViewModel.nonFictionBooksPagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                nonFictionAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeActiveVenues(){
        homeViewModel.activeVenuePagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                activeAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeSocialVenues(){
        homeViewModel.socialVenuePagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                socialAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void initializeEntertainmentVenues(){
        homeViewModel.entertainmentVenuePagedList.observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                entertainmentAdapter.submitList(data);
            }else{
                alertDatabaseError();
            }
        });
    }

    private void alertDatabaseError(){
        if(!hasShown){
            hasShown = true;
            new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                    .setMessage( "We experience errors while reading data from database.")
                    .setCancelable(false)
                    .setPositiveButton("TRY LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
    }

    public void goToSettingsActivity(){
        Intent intent = new Intent(requireActivity(), SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

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

    @Override
    public void onReloadClickListener() {
        reload();
    }

    public void reload(){
        recommendedAdapter.notifyDataSetChanged();
        foodAdapter.notifyDataSetChanged();
        breweryAdapter.notifyDataSetChanged();
        familyAdapter.notifyDataSetChanged();
        activeAdapter.notifyDataSetChanged();
        socialAdapter.notifyDataSetChanged();
        entertainmentAdapter.notifyDataSetChanged();
    }
}