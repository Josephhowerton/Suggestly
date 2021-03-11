package com.mortonsworld.suggest.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mortonsworld.suggest.ui.library.FavoriteListFragment;
import com.mortonsworld.suggest.ui.library.SavedListFragment;

public class LibraryStateAdapter extends FragmentStateAdapter {

    public LibraryStateAdapter(@NonNull Fragment fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        Bundle args;
        switch (position){
            case 0:
                fragment = new SavedListFragment();
                args = new Bundle();
                args.putInt(SavedListFragment.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                break;
            case 1:
                fragment = new FavoriteListFragment();
                args = new Bundle();
                args.putInt(FavoriteListFragment.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
