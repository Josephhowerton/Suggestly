package com.mortonsworld.suggest.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggest.R;
import com.mortonsworld.suggest.databinding.CardViewHomeBinding;
import com.mortonsworld.suggest.model.nyt.Book;

public class HomeBookAdapter extends PagedListAdapter<Book, HomeBookAdapter.HomeViewHolder> {

    private final BookSelectedListener listener;

    public HomeBookAdapter(@NonNull DiffUtil.ItemCallback<Book> diffCallback, BookSelectedListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewHomeBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.card_view_home, parent,
                false);

        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final Book book = getItem(position);
        holder.bind(book);
    }

    @Override
    public void submitList(@Nullable PagedList<Book> pagedList) {
        super.submitList(pagedList);
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private final CardViewHomeBinding binding;

        public HomeViewHolder(CardViewHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(Book book){
            binding.name.setText(book.getTitle());
            binding.categoryName.setText(book.getDisplayName());
            binding.distance.setText(book.getAuthor());
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.address.setText(book.getDescription());
            binding.venueImage.setVisibility(View.GONE);
            binding.cardView.setOnClickListener(view -> listener.onBookSelected(book));
            Glide.with(binding.getRoot())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.mainImage);
        }
    }

    public interface BookSelectedListener{
        void onBookSelected(Book book);
    }
}
