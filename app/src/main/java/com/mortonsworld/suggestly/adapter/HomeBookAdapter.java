package com.mortonsworld.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.CardViewHomeBinding;
import com.mortonsworld.suggestly.callbacks.SaveCallback;
import com.mortonsworld.suggestly.model.nyt.Book;

public class HomeBookAdapter extends PagedListAdapter<Book, HomeBookAdapter.HomeViewHolder> {

    private final BookSelectedListener listener;
    private final SaveCallback saveVenueListener;

    public HomeBookAdapter(@NonNull DiffUtil.ItemCallback<Book> diffCallback, BookSelectedListener listener, SaveCallback saveVenueListener) {
        super(diffCallback);
        this.listener = listener;
        this.saveVenueListener = saveVenueListener;
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
        if(book != null){
            holder.bind(book);
        }
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
            binding.cardView.setOnClickListener(view -> listener.onBookSelected(book));
            binding.saveImage.setOnClickListener(view -> saveVenueListener.onSuggestionSaved(book, getAdapterPosition()));
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
