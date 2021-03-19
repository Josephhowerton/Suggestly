package com.mortonsworld.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.CardViewHomeBinding;
import com.mortonsworld.suggestly.interfaces.SaveCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Category;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.relations.VenueAndCategory;
import com.mortonsworld.suggestly.model.user.LocationTuple;
import com.mortonsworld.suggestly.utility.DistanceCalculator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeVenueAdapter extends PagedListAdapter<VenueAndCategory, HomeVenueAdapter.HomeViewHolder> {

    private final VenueSelectedListener listener;
    private final SaveCallback saveVenueListener;
    private LocationTuple locationTuple;

    public HomeVenueAdapter(@NonNull DiffUtil.ItemCallback<VenueAndCategory> diffCallback, VenueSelectedListener listener, SaveCallback saveVenueListener) {
        super(diffCallback);
        this.listener = listener;
        this.saveVenueListener = saveVenueListener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_home, parent, false);
        binding.setSaveCallback(saveVenueListener);
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final VenueAndCategory venue = getItem(position);
        if(venue == null){
            return;
        }

        if(venue.venue != null && venue.category != null){
            holder.bindVenue(venue.venue);
            holder.bindCategory(venue.category);
            holder.bindImage(venue);
        }else{
            holder.clear();
        }
    }

    public void setLocationTuple(LocationTuple locationTuple){
        this.locationTuple = locationTuple;
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private final CardViewHomeBinding binding;

        public HomeViewHolder(@NotNull CardViewHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindVenue(@NotNull Venue venue){
            binding.name.setText(venue.getName());
            binding.distance.setText(formatDistance(venue));
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
            if(venue.venue.url != null){
                url = venue.venue.getBestPhotoUrl();
            }else{
                url = venue.category.getIconWithBGUrl(100);
            }

            Glide.with(binding.getRoot()).load(url)
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.mainImage);
        }

        public String formatDistance(Venue venue){
            if(locationTuple == null){
                return "";
            }

            return String.format(Locale.ENGLISH, "%.1f", DistanceCalculator.distanceMile(locationTuple.lat, venue.location.lat, locationTuple.lng, venue.location.lng))
                    + " miles";
        }

        public void clear(){

        }
    }

    public interface VenueSelectedListener{
        void onVenueSelected(Venue venue);
    }
}
