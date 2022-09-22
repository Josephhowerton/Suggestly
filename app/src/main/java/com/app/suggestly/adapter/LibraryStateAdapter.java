package com.app.suggestly.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.suggestly.ui.library.FavoriteListFragment;
import com.app.suggestly.ui.library.SavedListFragment;

public class LibraryStateAdapter extends FragmentStateAdapter {

    public LibraryStateAdapter(@NonNull Fragment fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        Bundle args;
        if (position == 1) {
            fragment = new FavoriteListFragment();
            args = new Bundle();
            args.putInt(FavoriteListFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
        } else {
            fragment = new SavedListFragment();
            args = new Bundle();
            args.putInt(SavedListFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
