package com.mortonsworld.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.CardViewHomeBinding;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;

import java.util.List;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder> {
    private final List<Suggestion> list;
    private final SuggestionSelectedListener listener;

    public SimilarAdapter(List<Suggestion> list, SuggestionSelectedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_home, parent, false);

        return new SimilarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        final Suggestion suggestion = list.get(position);
        switch (suggestion.getSuggestionType()){
            case FOURSQUARE_VENUE:
                break;

            case BOOK:
                holder.bindBook((Book)suggestion);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SimilarViewHolder extends RecyclerView.ViewHolder {
        private final CardViewHomeBinding binding;

        public SimilarViewHolder(CardViewHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindVenue(Venue venue){
            binding.name.setText(venue.getName());
            binding.address.setText(venue.location.address);
            binding.address.setMaxLines(2);
            Glide.with(binding.getRoot()).load(venue.url)
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.mainImage);
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(venue));
        }

        public void bindBook(Book book){
            binding.name.setText(book.getTitle());
            binding.categoryName.setText(book.getDisplayName());
            binding.distance.setText(book.getAuthor());
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.address.setVisibility(View.GONE);
            binding.saveImage.setVisibility(View.GONE);
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(book));
            Glide.with(binding.getRoot())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.mainImage);
        }
    }

    public interface SuggestionSelectedListener{
        void onVenueSelected(Suggestion suggestion);
    }
}