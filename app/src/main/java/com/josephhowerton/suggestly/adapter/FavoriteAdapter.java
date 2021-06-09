package com.josephhowerton.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.callbacks.FavoriteCallback;
import com.josephhowerton.suggestly.callbacks.SuggestionCallback;
import com.josephhowerton.suggestly.databinding.CardViewFavoriteBinding;
import com.josephhowerton.suggestly.model.Suggestion;
import com.josephhowerton.suggestly.model.foursquare.Category;
import com.josephhowerton.suggestly.model.foursquare.Location;
import com.josephhowerton.suggestly.model.foursquare.Venue;
import com.josephhowerton.suggestly.model.nyt.Book;
import com.josephhowerton.suggestly.model.relations.VenueAndCategory;
import com.josephhowerton.suggestly.utility.DistanceCalculator;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ListViewHolder> {
    private final List<Suggestion> suggestions;
    private final SuggestionCallback listener;
    private final FavoriteCallback favoriteListener;
    private final EmptyListListener emptyListListener;
    public FavoriteAdapter(List<Suggestion> suggestions, SuggestionCallback listener,
                           FavoriteCallback favoriteListener, EmptyListListener emptyListListener) {
        this.suggestions = suggestions;
        this.listener = listener;
        this.favoriteListener = favoriteListener;
        this.emptyListListener = emptyListListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewFavoriteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_favorite, parent, false);
        return new FavoriteAdapter.ListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);
        if(suggestion != null){
            holder.bindFavoriteAnimation(suggestion, position);
            switch (suggestion.getSuggestionType()){
                case BOOK:
                    bindBook(holder, (Book) suggestion);
                    break;
                case RECOMMENDED_VENUE:
                case FOURSQUARE_VENUE:
                    bindVenue(holder, (VenueAndCategory) suggestion);
                    break;
            }
        }
    }

    public void bindVenue(ListViewHolder holder, VenueAndCategory venue){
        if(venue.venue != null && venue.category != null){
            holder.bindVenue(venue.venue);
            holder.bindCategory(venue.category);
            holder.bindImage(venue);
        }
    }

    public void bindBook(ListViewHolder holder, Book book){
        if(book != null){
            holder.bindBook(book);
        }
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        private final CardViewFavoriteBinding binding;
        public ListViewHolder(@NonNull CardViewFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.saveImage.setChecked(true);
        }

        private void bindBook(Book book){
            binding.name.setText(book.getTitle());
            binding.categoryName.setText(book.getDisplayName());
            binding.distance.setText(book.getAuthor());
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.address.setText(book.getDescription());
            binding.cardView.setOnClickListener(view -> listener.onSuggestionSelected(book));
            Glide.with(binding.getRoot())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public void bindVenue(@NotNull Venue venue){
            binding.name.setText(venue.getName());
            binding.distance.setText(formatDistance(venue.location));
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.address.setMaxLines(2);
            binding.address.setText(venue.getFormattedAddress());
            binding.cardView.setOnClickListener(view -> listener.onSuggestionSelected(venue));
        }

        public void bindCategory(@NotNull Category category){
            binding.categoryName.setText(category.name);
        }

        public void bindImage(@NotNull VenueAndCategory venue){
            String url = "";
            if(venue.venue.url != null && !venue.venue.url.equals("") && !venue.venue.url.isEmpty()){
                url = venue.venue.getBestPhotoUrl();
            }else{
                url = venue.category.getIconUrl(100);
            }

            Glide.with(binding.getRoot()).load(url)
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public void bindFavoriteAnimation(Suggestion suggestion, int position) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
            binding.saveImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                favoriteListener.onSuggestionFavorite(suggestion, binding.saveImage.isChecked());
                suggestions.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, suggestions.size());
                if(suggestions.size() == 0){
                    emptyListListener.onEmptyList();
                }
            });
        }

        public String formatDistance(Location location) {
            return location.distance == null ? "unknown distance" : String.format(Locale.ENGLISH, "%.1f", DistanceCalculator.meterToMiles(location.distance)) + " miles";
        }
    }

    public interface EmptyListListener{
        void onEmptyList();
    }
}

