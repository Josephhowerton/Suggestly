package com.mortonsworld.suggestly.adapter;

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
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.callbacks.DetailsCallback;
import com.mortonsworld.suggestly.callbacks.FavoriteCallback;
import com.mortonsworld.suggestly.callbacks.SaveCallback;
import com.mortonsworld.suggestly.databinding.RowItemMoreBinding;
import com.mortonsworld.suggestly.model.Suggestion;
import com.mortonsworld.suggestly.model.nyt.Book;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoreFragmentAdapter extends RecyclerView.Adapter<MoreFragmentAdapter.MoreViewHolder> {
    private final List<Suggestion> suggestions;
    private List<Suggestion> savedSuggestions;
    private List<Suggestion> favoriteSuggestions;
    private final DetailsCallback listener;
    private final SaveCallback saveListener;
    private final FavoriteCallback favoriteListener;
    public MoreFragmentAdapter(List<Suggestion> suggestions, DetailsCallback detailsCallback, SaveCallback saveListener, FavoriteCallback favoriteListener){
        this.suggestions = suggestions;
        this.listener = detailsCallback;
        this.saveListener = saveListener;
        this.favoriteListener = favoriteListener;
    }
    @NonNull
    @Override
    public MoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemMoreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_item_more, parent, false);
        return new MoreViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreViewHolder holder, int position) {
        holder.bind(suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void setSavedSuggestions(List<Suggestion> savedSuggestions){
        this.savedSuggestions = savedSuggestions;
    }

    public void setFavoriteSuggestions(List<Suggestion> favoriteSuggestions){
        this.favoriteSuggestions = favoriteSuggestions;
    }

    public class MoreViewHolder extends RecyclerView.ViewHolder{
        RowItemMoreBinding binding;
        public MoreViewHolder(@NonNull RowItemMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Suggestion suggestion){
            bindAnimation(suggestion);
            bindFavoriteToggle(suggestion);
            bindSavedToggle(suggestion);
            switch (suggestion.getSuggestionType()){
                case FOURSQUARE_VENUE:
                    bindVenue((VenueAndCategory) suggestion);
                    break;
                case BOOK:
                    bindBook((Book) suggestion);
                    break;
            }
        }

        private void bindVenue(VenueAndCategory venueAndCategory){
            binding.name.setText(venueAndCategory.venue.getName());
            binding.address.setText(venueAndCategory.venue.location.getFormattedAddress());
            binding.getRoot().setOnClickListener(view -> listener.onSuggestionDetailsListener(venueAndCategory.venue));
            Glide.with(binding.getRoot())
                    .load(venueAndCategory.category.getIconWithBGUrl(64))
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.icon);
        }

        private void bindBook(Book book){
            binding.name.setText(book.getTitleWithAuthor());
            binding.address.setText(book.getDescription());
            Glide.with(binding.getRoot()).load(book.getBookImage())
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.icon);
            binding.getRoot().setOnClickListener(view -> listener.onSuggestionDetailsListener(book));
        }

        public void bindAnimation(Suggestion suggestion) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
            binding.saveToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                saveListener.onSuggestionSaved(suggestion, isChecked);
            });

            binding.favoriteToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                favoriteListener.onSuggestionFavorite(suggestion, isChecked);
            });
        }

        public void bindSavedToggle(@NotNull Suggestion suggestion){
            for(Suggestion temp: savedSuggestions){
                if(temp.getId().equals(suggestion.getId())){
                    binding.saveToggle.setChecked(true);
                    binding.favoriteToggle.setChecked(false);
                }else{
                    binding.saveToggle.setChecked(false);
                }
            }
        }

        public void bindFavoriteToggle(@NotNull Suggestion suggestion){
            for(Suggestion temp: favoriteSuggestions){
                if(temp.getId().equals(suggestion.getId())){
                    binding.favoriteToggle.setChecked(true);
                    binding.saveToggle.setChecked(false);
                }else{
                    binding.favoriteToggle.setChecked(false);
                }
            }
        }
    }
}
