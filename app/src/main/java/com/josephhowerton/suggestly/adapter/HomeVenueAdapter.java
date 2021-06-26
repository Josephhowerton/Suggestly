package com.josephhowerton.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.callbacks.SaveCallback;
import com.josephhowerton.suggestly.databinding.CardViewHomeBinding;
import com.josephhowerton.suggestly.app.model.foursquare.Category;
import com.josephhowerton.suggestly.app.model.foursquare.Location;
import com.josephhowerton.suggestly.app.model.foursquare.Venue;
import com.josephhowerton.suggestly.app.model.relations.VenueAndCategory;
import com.josephhowerton.suggestly.utility.DistanceCalculator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeVenueAdapter extends PagedListAdapter<VenueAndCategory, HomeVenueAdapter.HomeViewHolder> {
    private final VenueSelectedListener listener;
    private final SaveCallback saveVenueListener;
    private final List<VenueAndCategory> saved = new ArrayList<>();

    public HomeVenueAdapter(VenueSelectedListener listener, SaveCallback saveVenueListener) {
        super(VENUE_CALLBACK);
        this.listener = listener;
        this.saveVenueListener = saveVenueListener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_home, parent, false);
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final VenueAndCategory venue = getItem(position);
        if(venue != null){
            if(venue.venue != null && venue.category != null){
                holder.bindSavedImage(venue);
                holder.bindVenue(venue.venue);
                holder.bindCategory(venue.category);
                holder.bindImage(venue);
                holder.bindSavedAnimation(venue.venue);
            }
        }
    }

    public void setSavedList(List<VenueAndCategory> saved){
        this.saved.clear();
        this.saved.addAll(saved);
        notifyDataSetChanged();
    }

    @Override
    public void submitList(@Nullable PagedList<VenueAndCategory> pagedList) {
        super.submitList(pagedList);
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private final CardViewHomeBinding binding;

        public HomeViewHolder(@NotNull CardViewHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindVenue(@NotNull Venue venue){
            binding.name.setText(venue.getName());
            binding.distance.setText(formatDistance(venue.location));
            binding.distance.setTypeface(Typeface.DEFAULT_BOLD);
            binding.address.setMaxLines(2);
            binding.address.setText(venue.getFormattedAddress());
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(venue));
        }

        public void bindCategory(@NotNull Category category){
            binding.categoryName.setText(category.name);
        }

        public void bindImage(@NotNull VenueAndCategory venue){
            String url = "";
            if(venue.venue.url != null && !venue.venue.url.equals("") && !venue.venue.url.isEmpty()){
                url = venue.venue.getBestPhotoUrl();
                binding.mainImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else{
                url = venue.category.getIconUrl(100);
            }

            Glide.with(binding.getRoot()).load(url)
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public void bindSavedImage(@NotNull VenueAndCategory venueAndCategory){
            for(VenueAndCategory temp: saved){
                if(temp.getId().equals(venueAndCategory.getId())){
                    binding.saveImage.setChecked(true);
                }
            }
        }

        public void bindSavedAnimation(Venue venue) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
            binding.saveImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                saveVenueListener.onSuggestionSaved(venue, isChecked);
            });
        }

        public String formatDistance(Location location) {
            return location.distance == null ? "unknown distance" : String.format(Locale.ENGLISH, "%.1f", DistanceCalculator.meterToMiles(location.distance)) + " miles";
        }

    }

    public interface VenueSelectedListener{
        void onVenueSelected(Venue venue);
    }

    private static final DiffUtil.ItemCallback<VenueAndCategory> VENUE_CALLBACK = new DiffUtil.ItemCallback<VenueAndCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull VenueAndCategory oldItem, @NonNull VenueAndCategory newItem) {
            return oldItem.venue.getId().equals(newItem.venue.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull VenueAndCategory oldItem, @NonNull VenueAndCategory newItem) {
            return oldItem.venue.equals(newItem.venue);
        }
    };
}
