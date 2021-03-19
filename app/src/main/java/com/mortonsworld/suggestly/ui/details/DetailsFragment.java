package com.mortonsworld.suggestly.ui.details;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.FragmentDetailsBinding;
import com.mortonsworld.suggestly.interfaces.DetailsCallback;
import com.mortonsworld.suggestly.interfaces.FavoriteCallback;
import com.mortonsworld.suggestly.interfaces.MoreCallback;
import com.mortonsworld.suggestly.interfaces.SaveCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.ui.main.MainActivity;
import com.mortonsworld.suggestly.utility.Config;
import com.mortonsworld.suggestly.utility.SuggestionType;

import org.jetbrains.annotations.NotNull;

public class DetailsFragment extends Fragment implements MoreCallback, DetailsCallback, SaveCallback, FavoriteCallback {

    private DetailsViewModel mViewModel;
    private FragmentDetailsBinding binding;
    private ActionBar actionBar;
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
        actionBar = ((MainActivity)requireActivity()).getSupportActionBar();
        Bundle bundle = getArguments();
        if(bundle != null){
            String id = bundle.getString(Config.DETAILS_SUGGESTION_ID_KEY);
            SuggestionType type = (SuggestionType) bundle.getSerializable(Config.DETAILS_SUGGESTION_TYPE_KEY);

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
        }


        return binding.getRoot();
    }

    public void initializeFoursquare(String id){
        mViewModel.readVenueDetails(id).observe(getViewLifecycleOwner(), venue -> {
            Log.println(Log.ASSERT, "DETAILS", venue.getName());
            if(actionBar != null){
                actionBar.setTitle(venue.getName());
            }

            if(venue.bestPhoto != null){
                initVenueImage(venue.bestPhoto);
            }
        });
    }

    public void initVenueImage(Venue.BestPhoto bestPhoto){
        Glide.with(requireContext())
                .load(bestPhoto.getPrefix() + "540X540" + bestPhoto.suffix)
                .placeholder(R.drawable.progress_bar)
                .into(binding.bestPhoto);
    }

    public void initializeBook(String isbn13){
        mViewModel.readBookDetails(isbn13).observe(getViewLifecycleOwner(), book -> {
            if(actionBar != null){
                actionBar.setTitle(book.getTitle());
            }

            Glide.with(requireContext())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.bestPhoto);
        });
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
    public void onSuggestionSaved() {

    }

    @Override
    public void onSuggestionFavorite() {

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
}