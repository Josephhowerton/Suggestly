package com.mortonsworld.suggestly.ui.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.adapter.SimilarAdapter;
import com.mortonsworld.suggestly.adapter.SimilarCategoryAdapter;
import com.mortonsworld.suggestly.databinding.FragmentDetailsBinding;
import com.mortonsworld.suggestly.callbacks.DetailsCallback;
import com.mortonsworld.suggestly.callbacks.FavoriteCallback;
import com.mortonsworld.suggestly.callbacks.MoreCallback;
import com.mortonsworld.suggestly.callbacks.SaveCallback;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsFragment extends Fragment implements MoreCallback, DetailsCallback, SaveCallback, FavoriteCallback,
        SimilarAdapter.SuggestionSelectedListener,SimilarCategoryAdapter.CategorySelectedListener{
    private final List<Suggestion> list = new ArrayList<>();
    private SimilarAdapter similarAdapter;
    private DetailsViewModel mViewModel;
    private FragmentDetailsBinding binding;
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

        initializeToolbar();
        similarAdapter = new SimilarAdapter(list, this);
        Bundle bundle = getArguments();
        if(bundle != null){
            String id = bundle.getString(Config.DETAILS_SUGGESTION_ID_KEY);
            String title = bundle.getString(Config.DETAILS_SUGGESTION_TITLE_KEY);
            SuggestionType type = (SuggestionType) bundle.getSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY);
            binding.toolbar.setTitle(title);
            if(type != null && id != null){
                switch (type){
                    case FOURSQUARE_VENUE:
                        initializeFoursquare(id);
                        break;
                    case BOOK:
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
                        isShow = true;
                    } else if(isShow) {
                        binding.collapsingToolbar.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
        }



        return binding.getRoot();
    }

    public void initializeFoursquare(String id){
        mViewModel.readVenueDetails(id).observe(getViewLifecycleOwner(), venue -> {
            binding.setSuggestion(venue);
            if(!venue.hasDetails){
                mViewModel.getFoursquareVenuesDetails(venue);
                mViewModel.getFoursquareVenuesSimilar(venue);
                binding.loading.loadingLayout.setVisibility(View.VISIBLE);
                return;
            }else{
                binding.loading.loadingLayout.setVisibility(View.GONE);
            }
            initializeSimilarVenueRecyclerView(venue);
            initializeSimilarCategoryRecyclerView(venue);
            loadVenueDisplay(venue);
            initVenueImage(venue);
        });
    }


    public void loadVenueDisplay(Venue venue){
        binding.shortDescription.setText(venue.getDescription());
        binding.address.setText(venue.getFormattedAddress());
        //todo set distance;
    }

    public void initVenueImage(Venue venue){
        if(venue.bestPhoto != null){
            Glide.with(requireContext())
                    .load(venue.getBestPhotoUrl())
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.bestPhoto);
        }
    }

    private void initializeSimilarVenueRecyclerView(Venue venue){
        binding.similarRecycler.setAdapter(similarAdapter);
        binding.similarTitle.setText(getString(R.string.title_similar_venues));
        binding.similarRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mViewModel.readFoursquareVenuesSimilar(venue).observe(getViewLifecycleOwner(), venueAndCategories -> {
            list.clear();
            list.addAll(venueAndCategories);
            similarAdapter.notifyDataSetChanged();
        });
    }

    private void initializeSimilarCategoryRecyclerView(Venue venue) {
        SimilarCategoryAdapter similarCategoryAdapter = new SimilarCategoryAdapter(diffUtil, this);
        binding.categoriesRecyclerView.setAdapter(similarCategoryAdapter);
        binding.similarRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mViewModel.readRelatedCategories(venue.categoryId).observe(getViewLifecycleOwner(), similarCategoryAdapter::submitList);
    }

    public void initializeBook(String isbn13){
        mViewModel.readBookDetails(isbn13).observe(getViewLifecycleOwner(), book -> {
            binding.setSuggestion(book);
            initializeBookRecyclerView(book);
            loadBookDisplay(book);
            initBookImage(book);
        });
    }

    public void loadBookDisplay(Book book){
        binding.genreButton.setText(book.displayName);
        binding.authorDescription.setText(getString(R.string.written_by, book.getContributor()));
        binding.publisherDescription.setText(getString(R.string.publish_by, book.getPublisher()));
        binding.shortDescription.setText(book.getDescription());
        binding.takeActionButton.setOnClickListener(view -> goToWebFragment(book.getAmazonProductUrl()));
        binding.genreButton.setOnClickListener(view -> navigateToList(book.getSuggestionType(), book.getListNameEncoded(), book.getDisplayName()));
        String week = book.getWeeksOnList() > 1 ? "weeks" : "week";
        binding.rankWeeksDescription.setText(getString(R.string.rank_weeks_description,
                book.getTitle(), String.valueOf(book.getRank()), String.valueOf(book.getWeeksOnList()),
                week));
    }

    private void initializeBookRecyclerView(Book book){
        list.addAll(mViewModel.readNewYorkTimesBestsellingListLimitThree(book.getPrimaryIsbn13(), book.listNameEncoded));
        binding.similarRecycler.setAdapter(similarAdapter);
        binding.similarTitle.setText(getString(R.string.title_other_books));
        binding.similarRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
    }

    public void initBookImage(Book book){
        Glide.with(requireContext())
                .load(book.getBookImage())
                .placeholder(R.drawable.progress_bar)
                .into(binding.bestPhoto);
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

    @Override
    public void onMoreSuggestions(@NotNull SuggestionType type, @NotNull String id, @NotNull String title) {
        navigateToList(type, id, title);
    }

    @Override
    public void onSuggestionSaved(Suggestion suggestion, int position) {

    }

    @Override
    public void onSuggestionFavorite() {

    }

    @Override
    public void onVenueSelected(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onCategorySelected(Category category) {
        navigateToList(SuggestionType.FOURSQUARE_VENUE, category.getId(), category.getName());
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

    public void goToWebFragment(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    DiffUtil.ItemCallback<Category> diffUtil = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return false;
        }
    };

}