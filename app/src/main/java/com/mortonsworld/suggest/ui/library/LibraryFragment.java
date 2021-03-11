package com.mortonsworld.suggest.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.databinding.FragmentLibraryBinding;
import com.mortonsworld.suggest.adapter.LibraryStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentLibraryBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_library, container, false);

        LibraryStateAdapter adapter = new LibraryStateAdapter(this);
        binding.viewPager.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(SavedListFragment.ARG_OBJECT);
                    break;
                case 1:
                    tab.setText(FavoriteListFragment.ARG_OBJECT);
                    break;
            }
        });
        mediator.attach();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LibraryViewModel notificationsViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
    }
}