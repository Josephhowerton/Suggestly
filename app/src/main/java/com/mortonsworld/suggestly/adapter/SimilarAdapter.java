package com.mortonsworld.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.CardViewSimilarBinding;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Location;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.utility.DistanceCalculator;

import java.util.List;
import java.util.Locale;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder> {
    private final SuggestionSelectedListener listener;
    private final List<Suggestion> list;

    public SimilarAdapter(List<Suggestion> list, SuggestionSelectedListener listener) {
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewSimilarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_similar, parent, false);

        return new SimilarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        final Suggestion suggestion = list.get(position);
        switch (suggestion.getSuggestionType()){
            case FOURSQUARE_VENUE:
                holder.bindVenue((VenueAndCategory)suggestion);
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
        private final CardViewSimilarBinding binding;

        public SimilarViewHolder(CardViewSimilarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindVenue(VenueAndCategory venue){
            binding.name.setText(venue.venue.getName());
            binding.distance.setMaxLines(2);
            binding.categoryName.setText(venue.category.name);
            binding.distance.setText(formatDistance(venue.venue.location));
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(venue));
            Glide.with(binding.getRoot()).load(venue.category.getIconUrl(64))
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public void bindBook(Book book){
            binding.name.setText(book.getTitle());
            binding.categoryName.setText(book.displayName);
            binding.distance.setText(book.getAuthor());
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(book));
            Glide.with(binding.getRoot())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public String formatDistance(Location location) {
            return location.distance == null ? "unknown distance" : String.format(Locale.ENGLISH, "%.1f", DistanceCalculator.meterToMiles(location.distance)) + " miles";
        }
    }

    public interface SuggestionSelectedListener{
        void onVenueSelected(Suggestion suggestion);
    }
}