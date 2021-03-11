package com.mortonsworld.suggest.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.databinding.FragmentFavoritesListBinding;
import com.mortonsworld.suggest.databinding.FragmentLibraryBinding;

public class FavoriteListFragment extends Fragment {
    public static final String ARG_OBJECT = "Favorites";
    private LibraryViewModel libraryViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentFavoritesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites_list, container, false);

        libraryViewModel.userLiveData.observe(getViewLifecycleOwner(), binding::setUser);
        binding.addToFavoritesButton.setOnClickListener(view -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_search));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}