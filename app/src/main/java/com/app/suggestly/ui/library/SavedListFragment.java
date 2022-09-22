package com.app.suggestly.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.suggestly.R;
import com.app.suggestly.adapter.SavedAdapter;
import com.app.suggestly.callbacks.SaveCallback;
import com.app.suggestly.callbacks.SuggestionCallback;
import com.app.suggestly.databinding.FragmentSavedListBinding;
import com.app.suggestly.app.model.Suggestion;
import com.app.suggestly.app.model.foursquare.Venue;
import com.app.suggestly.app.model.nyt.Book;
import com.app.suggestly.utility.Config;

import java.util.ArrayList;
import java.util.List;

public class SavedListFragment extends Fragment implements SuggestionCallback, SaveCallback, SavedAdapter.EmptyListListener {
    public static final String ARG_OBJECT = "Saved";
    private LibraryViewModel libraryViewModel;
    private final List<Suggestion> suggestions = new ArrayList<>();
    private FragmentSavedListBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_list, container, false);

        SavedAdapter savedAdapter = new SavedAdapter(suggestions, this, this, this);
        binding.recyclerView.setAdapter(savedAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));

        libraryViewModel.readSavedSuggestions().observe(getViewLifecycleOwner(), savedSuggestions -> {
            if(!savedSuggestions.isEmpty()){
                suggestions.clear();
                suggestions.addAll(savedSuggestions);
                savedAdapter.notifyDataSetChanged();
                binding.addSavedButton.setVisibility(View.GONE);
            }else{
                onEmptyList();
            }
            binding.loadingLayout.loadingLayout.setVisibility(View.GONE);
        });
        binding.addSavedButton.setOnClickListener(view -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_search));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSuggestionSelected(Suggestion suggestion) {
        navigateToDetails(suggestion);
    }

    @Override
    public void onSuggestionSaved(Suggestion suggestion, Boolean isChecked) {
        libraryViewModel.deleteSavedSuggestion(suggestion);
    }

    @Override
    public void onEmptyList() {
        binding.titleAddToSave.setVisibility(View.VISIBLE);
        binding.addSavedImage.setVisibility(View.VISIBLE);
        binding.addSavedButton.setVisibility(View.VISIBLE);
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

}