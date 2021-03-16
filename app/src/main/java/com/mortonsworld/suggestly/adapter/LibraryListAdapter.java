package com.mortonsworld.suggestly.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mortonsworld.suggestly.interfaces.Suggestion;

import org.jetbrains.annotations.NotNull;

public class LibraryListAdapter extends PagedListAdapter<Suggestion, LibraryListAdapter.ListViewHolder> {

    public LibraryListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static final DiffUtil.ItemCallback<Suggestion> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Suggestion>() {
                // Concert details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(Suggestion oldConcert, Suggestion newConcert) {
                    return oldConcert.getId() == newConcert.getId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Suggestion oldConcert,
                                                  @NotNull Suggestion newConcert) {
                    return oldConcert.equals(newConcert);
                }
            };
}
