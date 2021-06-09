package com.josephhowerton.suggestly.ui.details;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.adapter.BuyLinksAdapter;
import com.josephhowerton.suggestly.adapter.SimilarAdapter;
import com.josephhowerton.suggestly.adapter.SimilarCategoryAdapter;
import com.josephhowerton.suggestly.callbacks.DetailsCallback;
import com.josephhowerton.suggestly.callbacks.FavoriteCallback;
import com.josephhowerton.suggestly.callbacks.MoreCallback;
import com.josephhowerton.suggestly.callbacks.SaveCallback;
import com.josephhowerton.suggestly.databinding.FragmentDetailsBinding;
import com.josephhowerton.suggestly.model.Suggestion;
import com.josephhowerton.suggestly.model.foursquare.Location;
import com.josephhowerton.suggestly.model.foursquare.Venue;
import com.josephhowerton.suggestly.model.nyt.Book;
import com.josephhowerton.suggestly.model.relations.CategoryTuple;
import com.josephhowerton.suggestly.model.relations.VenueAndCategory;
import com.josephhowerton.suggestly.utility.Config;
import com.josephhowerton.suggestly.utility.DistanceCalculator;
import com.josephhowerton.suggestly.utility.NetworkHandler;
import com.josephhowerton.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailsFragment extends Fragment implements MoreCallback, DetailsCallback, SaveCallback, FavoriteCallback,
        SimilarAdapter.SuggestionSelectedListener,SimilarCategoryAdapter.CategorySelectedListener, BuyLinksAdapter.BuySelectedBookListener {
    private final List<Suggestion> list = new ArrayList<>();
    private SimilarAdapter similarAdapter;
    private DetailsViewModel mViewModel;
    private FragmentDetailsBinding binding;
    private final String MESSAGE = "Check your Internet connection for Suggestion details.";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(DetailsViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        binding.loading.loadingLayout.setVisibility(View.VISIBLE);
        initializeToolbar();
        similarAdapter = new SimilarAdapter(list, this);
        Bundle bundle = getArguments();
        if(bundle != null){
            String id = bundle.getString(Config.DETAILS_SUGGESTION_ID_KEY);
            String title = bundle.getString(Config.DETAILS_SUGGESTION_TITLE_KEY);
            SuggestionType type = (SuggestionType) bundle.getSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY);
            binding.toolbar.setTitle(title);
            binding.suggestionTitle.setText(title);
            if(type != null && id != null){
                switch (type){
                    case FOURSQUARE_VENUE:
                        binding.venueDetails.nested.setVisibility(View.VISIBLE);
                        initializeFoursquare(id);
                        break;
                    case BOOK:
                        binding.bookDetails.nested.setVisibility(View.VISIBLE);
                        initializeBook(id);
                        break;
                }
            }

            binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = true;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        binding.collapsingToolbar.setTitle(title);
                        binding.suggestionTitle.setVisibility(View.GONE);
                        isShow = true;
                    } else if(isShow) {
                        binding.collapsingToolbar.setTitle(" ");
                        binding.suggestionTitle.setVisibility(View.VISIBLE);
                        isShow = false;
                    }
                }
            });
        }



        return binding.getRoot();
    }

    private void initializeFoursquare(String id){
        mViewModel.readVenueDetails(id).observe(getViewLifecycleOwner(), venue -> {
            binding.venueDetails.takeActionButton.setOnClickListener(view -> goToMaps(venue.location.lat, venue.location.lng));
            if(venue != null){
                bindVenueAnimation(venue);
                if(!venue.hasDetails){
                    if(NetworkHandler.isNetworkConnectionActive(requireActivity())){
                        mViewModel.getFoursquareVenuesDetails(venue);
                        mViewModel.getFoursquareVenuesSimilar(venue);
                        binding.loading.loadingLayout.setVisibility(View.VISIBLE);
                        return;
                    }else{
                        NetworkHandler.notifyBadConnectionAndGoBack(requireActivity(), MESSAGE);

                    }
                }else{
                    binding.loading.loadingLayout.setVisibility(View.GONE);
                }
                loadSimilarVenueRecyclerView(venue);
                loadSimilarCategoryRecyclerView(venue);
                loadVenueDisplay(venue);
                loadVenueImage(venue);
            }
            else {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while reading data from database.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            requireActivity().onBackPressed();
                        }).show();
            }
        });
    }

    private void loadSimilarVenueRecyclerView(Venue venue){
        binding.venueDetails.similarRecycler.setAdapter(similarAdapter);
        binding.venueDetails.similarTitle.setText(getString(R.string.title_similar_venues));
        binding.venueDetails.similarRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mViewModel.readFoursquareVenuesSimilar(venue).observe(getViewLifecycleOwner(), venueAndCategories -> {
            if(venueAndCategories != null){
                if(!venueAndCategories.isEmpty()){
                    list.clear();
                    list.addAll(venueAndCategories);
                    similarAdapter.notifyDataSetChanged();
                }else{
                    binding.venueDetails.similarTitle.setVisibility(View.GONE);
                    binding.venueDetails.similarRecycler.setVisibility(View.GONE);
                }
            }
            else{
                new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while reading data from database.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        });
    }

    private void loadSimilarCategoryRecyclerView(@NotNull Venue venue) {
        SimilarCategoryAdapter similarCategoryAdapter = new SimilarCategoryAdapter(diffUtil, this);
        binding.venueDetails.categoriesRecyclerView.setAdapter(similarCategoryAdapter);
        binding.venueDetails.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mViewModel.readRelatedCategories(venue.categoryId).observe(getViewLifecycleOwner(), categoryTuples -> {
            if(categoryTuples != null){
                if(!categoryTuples.isEmpty()){
                    similarCategoryAdapter.submitList(categoryTuples);
                }else{
                    binding.venueDetails.similarRecycler.setVisibility(View.GONE);
                    binding.venueDetails.similarTitle.setVisibility(View.GONE);
                }
            }else{
                new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                        .setMessage( "We experience errors while wiring data together for this suggestion.")
                        .setCancelable(false)
                        .setPositiveButton("TRY LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        });
    }

    private void loadVenueDisplay(Venue venue){
        loadDescription(venue);
        loadAddress(venue);
        loadRating(venue);
        loadContact(venue);
        loadStatus(venue);
        loadUrl(venue);
    }


    private void loadDescription(@NotNull Venue venue){
        if(venue.getDescription() != null && !venue.getDescription().equals("") && !venue.getDescription().isEmpty()){
            binding.venueDetails.shortDescription.setText(venue.getDescription());
        }else{
            binding.venueDetails.shortDescription.setVisibility(View.GONE);
            binding.venueDetails.descriptionTitle.setVisibility(View.GONE);
        }
    }

    private void loadAddress(@NotNull Venue venue){
        if(venue.getFormattedAddress() != null && !venue.getFormattedAddress().equals("") && !venue.getFormattedAddress().isEmpty()){
            binding.venueDetails.address.setText(venue.getFormattedAddress());
            binding.venueDetails.distance.setText(formatDistance(venue.location));
        }else{
            binding.venueDetails.address.setText(formatCoordinates(venue.location.lat, venue.location.lng));
        }
    }

    private String formatDistance(@NotNull Location location) {
        return location.distance == null ? "unknown distance" : String.format(Locale.ENGLISH, "%.1f", DistanceCalculator.meterToMiles(location.distance)) + " miles";
    }

    private String formatCoordinates(double lat, double lng) {
        return String.format(Locale.ENGLISH, "Latitude: %.1f, Longitude: %.2f", lat, lng);
    }

    private void loadRating(@NotNull Venue venue){
        boolean shouldLoad = (venue.rating != null && venue.rating > 0);
        binding.venueDetails.ratingTitle.setText(shouldLoad ? getString(R.string.rating, String.valueOf(venue.rating)) : "No Rating");
        if(shouldLoad){
            loadRatedColor(venue.ratingColor);
        }
    }

    private void loadContact(@NotNull Venue venue){
        if(venue.contact != null){
            if(venue.contact.phone != null && !venue.contact.phone.equals("") && !venue.contact.phone.isEmpty()){
                binding.venueDetails.phoneButton.setOnClickListener(view -> goToPhone(venue.contact.phone));
            }else{
                binding.venueDetails.phoneButton.setBackgroundTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.quantum_grey200, null)
                ));
                binding.venueDetails.phoneButton.setEnabled(false);
            }

            if(venue.url != null && !venue.url.equals("") && !venue.url.isEmpty()){
                binding.venueDetails.websiteButton.setOnClickListener(view -> goToUrl(venue.url));
            }else{
                binding.venueDetails.websiteButton.setBackgroundTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.quantum_grey200, null)
                ));
                binding.venueDetails.websiteButton.setEnabled(false);
            }

            if(venue.contact.facebookName != null && !venue.contact.facebookName.equals("") && !venue.contact.facebookName.isEmpty()){
                binding.venueDetails.facebookButton.setOnClickListener(view -> goToFacebook(venue.contact.facebookUsername));
            }else{
                binding.venueDetails.facebookButton.setBackgroundTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.quantum_grey200, null)
                ));
                binding.venueDetails.facebookButton.setEnabled(false);
            }

            if(venue.contact.instagram != null && !venue.contact.instagram.equals("") && !venue.contact.instagram.isEmpty()){
                binding.venueDetails.instagramButton.setOnClickListener(view -> goToInstagram(venue.contact.instagram ));
            }else{
                binding.venueDetails.instagramButton.setBackgroundTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.quantum_grey200, null)
                ));
                binding.venueDetails.instagramButton.setEnabled(false);
            }

            if(venue.contact.twitter != null && !venue.contact.twitter.equals("") && !venue.contact.twitter.isEmpty()){
                binding.venueDetails.twitterButton.setOnClickListener(view -> goToTwitter(venue.contact.twitter));
            }else{
                binding.venueDetails.twitterButton.setBackgroundTintList(ColorStateList.valueOf(
                        ResourcesCompat.getColor(getResources(), R.color.quantum_grey200, null)
                ));
                binding.venueDetails.twitterButton.setEnabled(false);
            }
        }
    }

    private void goToFacebook(String facebook_user_name){
        try {
            requireContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + facebook_user_name));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebook_user_name));
            startActivity(intent);
        }
    }

    private void goToUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void goToPhone(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void goToInstagram(String instagram_user_name){
        Uri uri = Uri.parse("http://instagram.com/_u/" + instagram_user_name);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/" + instagram_user_name)));
        }
    }

    private void goToTwitter(String twitter_user_name){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitter_user_name)));
        }catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitter_user_name)));
        }
    }

    private void loadRatedColor(String ratingColor){
        try{
            int color = Color.parseColor("#" + ratingColor);
            binding.venueDetails.ratingTitle.setTextColor(color);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void loadStatus(@NotNull Venue venue){
        if(venue.hours != null){
            boolean shouldLoad = (venue.hours.status != null && !venue.hours.status.equals("") && !venue.hours.status.isEmpty());
            binding.venueDetails.statusTitle.setText(shouldLoad ? venue.hours.status : getString(R.string.title_status_unknown));
        }else{
            binding.venueDetails.statusTitle.setText(R.string.title_status_unknown);
        }
    }

    private void loadUrl(Venue venue){
        binding.bookDetails.takeActionButton.setOnClickListener(view -> goToMaps(venue.location.lat, venue.location.lng));
    }

    private void loadVenueImage(@NotNull Venue venue){
        if(venue.bestPhoto != null){
            binding.bestPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(requireContext())
                    .load(venue.getBestPhotoUrl())
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.bestPhoto);
        }
    }

    private void initializeBook(String isbn13){
        mViewModel.readBookDetails(isbn13).observe(getViewLifecycleOwner(), book -> {
            binding.loading.loadingLayout.setVisibility(View.GONE);
            bindBookAnimation(book);
            initializeBookRecyclerView(book);
            loadBookDisplay(book);
            initBookImage(book);
        });
    }

    private void loadBookDisplay(@NotNull Book book){
        binding.bookDetails.genreButton.setText(book.displayName);
        binding.bookDetails.shortDescription.setText(book.getDescription());
        binding.bookDetails.takeActionButton.setOnClickListener(view -> goToWebFragment(book.getAmazonProductUrl()));
        binding.bookDetails.genreButton.setOnClickListener(view -> navigateToList(book.getSuggestionType(), book.getListNameEncoded(), book.getDisplayName()));


        initializeAdditionalButtons(book);
        initializeInformationDescription(book);
    }

    public void initializeInformationDescription(Book book){
        String week = book.getWeeksOnList() > 1 ? "weeks" : "week";
        binding.bookDetails.infoDescription.setText(String.format(getString(R.string.message_information),
                Html.fromHtml(book.getTitle()),
                Html.fromHtml(book.getContributor()),
                book.getPublisher(),
                book.getWeeksOnList(),
                book.getRank(), week)
        );
    }

    private void initializeAdditionalButtons(Book book){
        BuyLinksAdapter buyLinksAdapter = new BuyLinksAdapter(book.getBuyLinks(), this);
        binding.bookDetails.buyLinksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.bookDetails.buyLinksRecyclerView.setAdapter(buyLinksAdapter);
    }

    private void initializeBookRecyclerView(@NotNull Book book){
        List<Suggestion> books = mViewModel.readNewYorkTimesBestsellingListLimitThree(book.getPrimaryIsbn13(), book.listNameEncoded);
        if(books != null){
            list.addAll(books);
            binding.bookDetails.similarRecycler.setAdapter(similarAdapter);
            binding.bookDetails.similarRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        }else{
            new AlertDialog.Builder(requireContext()).setTitle("Database Error")
                    .setMessage( "We experience errors while wiring data together for this suggestion.")
                    .setCancelable(false)
                    .setPositiveButton("TRY LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
    }

    public void initBookImage(@NotNull Book book){
        Glide.with(requireContext())
                .load(book.getBookImage())
                .placeholder(R.drawable.glide_progress_bar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.bestPhoto);
    }

    private void initializeToolbar(){
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);
    }


    @Override
    public void onSuggestionDetailsListener(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onMoreSuggestions(@NotNull SuggestionType type, @NotNull String id, @NotNull String title) {
        navigateToList(type, id, title);
    }

    @Override
    public void onVenueSelected(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onCategorySelected(@NotNull CategoryTuple category) {
        navigateToList(SuggestionType.FOURSQUARE_VENUE, category.category_id, category.category_name);
    }

    private void navigateToList(SuggestionType type, String id, String title){
        Bundle bundle = new Bundle();
        bundle.putString(Config.LIST_SUGGESTION_ID_KEY, id);
        bundle.putString(Config.LIST_SUGGESTION_TITLE_KEY, title);
        bundle.putSerializable(Config.LIST_SUGGESTION_TYPE_KEY, type);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_more, bundle);
    }

    private void navigateToDetails(@NotNull Suggestion suggestion){
        Bundle bundle = new Bundle();
        bundle.putString(Config.DETAILS_SUGGESTION_ID_KEY, suggestion.getId());
        bundle.putString(Config.DETAILS_SUGGESTION_TITLE_KEY, getSuggestionTitle(suggestion));
        bundle.putSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY, suggestion.getSuggestionType());
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_details, bundle);
    }

    private String getSuggestionTitle(@NotNull Suggestion suggestion){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                return ((Book) suggestion).getTitle();
            case FOURSQUARE_VENUE:
                return ((VenueAndCategory) suggestion).venue.getName();
            default:
                return "Suggestion Details";
        }
    }

    private void goToWebFragment(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    DiffUtil.ItemCallback<CategoryTuple> diffUtil = new DiffUtil.ItemCallback<CategoryTuple>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryTuple oldItem, @NonNull CategoryTuple newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryTuple oldItem, @NonNull CategoryTuple newItem) {
            return false;
        }
    };

    private void bindVenueAnimation(@NotNull Venue suggestion) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        for(VenueAndCategory venue: mViewModel.getSavedVenues()){
            if(venue.getId().equals(suggestion.getId())){
                binding.venueDetails.saveToggle.setChecked(true);
            }
        }

        binding.venueDetails.saveToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            compoundButton.startAnimation(scaleAnimation);
            onSuggestionSaved(suggestion, isChecked);
        });

        for(VenueAndCategory venue: mViewModel.getFavoriteVenues()){
            if(venue.getId().equals(suggestion.getId())){
                binding.venueDetails.favoriteToggle.setChecked(true);
            }
        }

        binding.venueDetails.favoriteToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            compoundButton.startAnimation(scaleAnimation);
            onSuggestionFavorite(suggestion, isChecked);
        });
    }


    private void bindBookAnimation(@NotNull Book book) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        for(Book temp: mViewModel.getSavedBooks()){
            if(temp.getPrimaryIsbn13().equals(book.getPrimaryIsbn13())){
                binding.bookDetails.saveToggle.setChecked(true);
            }
        }
        binding.bookDetails.saveToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            compoundButton.startAnimation(scaleAnimation);
            onSuggestionSaved(book, isChecked);
        });

        for(Book temp: mViewModel.getFavoriteBooks()){
            if(temp.getPrimaryIsbn13().equals(book.getPrimaryIsbn13())){
                binding.bookDetails.favoriteToggle.setChecked(true);
            }
        }

        binding.bookDetails.favoriteToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            compoundButton.startAnimation(scaleAnimation);
            onSuggestionFavorite(book, isChecked);
        });
    }

    @Override
    public void onSuggestionSaved(Suggestion suggestion, @NotNull Boolean isChecked) {
        updateSavedSuggestion(suggestion, isChecked);
    }

    @Override
    public void onSuggestionFavorite(Suggestion suggestion, Boolean isFavorite) {
        updateFavoriteSuggestion(suggestion, isFavorite);
    }

    @Override
    public void onBuySelectedBook(String url) {
        goToUrl(url);
    }

    private void updateSavedSuggestion(@NotNull Suggestion suggestion, Boolean isSaved){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                mViewModel.updateBookSavedInUser((Book) suggestion, isSaved);
                break;

            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                mViewModel.updateVenueSavedInUser((Venue) suggestion, isSaved);
                break;
        }
    }

    private void updateFavoriteSuggestion(@NotNull Suggestion suggestion, Boolean isFavorite){
        switch (suggestion.getSuggestionType()){
            case BOOK:
                mViewModel.updateBookFavoriteInUser((Book) suggestion, isFavorite);
                break;

            case FOURSQUARE_VENUE:
            case RECOMMENDED_VENUE:
                mViewModel.updateVenueFavoriteInUser((Venue) suggestion, isFavorite);
                break;
        }
    }

    private void goToMaps(double lat, double lon){
        String geo = "geo:" + lat + "," + lon;
        Uri gmmIntentUri = Uri.parse(geo);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try{
            startActivity(mapIntent);
        }catch (Exception e){
            new AlertDialog.Builder(requireContext()).setTitle("Error")
                    .setMessage( "Suggest could not locate your maps app.")
                    .setCancelable(false)
                    .setPositiveButton("TRY LATER", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
    }
}