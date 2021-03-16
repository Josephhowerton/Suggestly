package com.mortonsworld.suggestly.adapter;

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
import com.mortonsworld.suggestly.model.foursquare.Venue;

public class HomeVenueAdapter extends PagedListAdapter<Venue, HomeVenueAdapter.HomeViewHolder> {

    private final VenueSelectedListener listener;

    public HomeVenueAdapter(@NonNull DiffUtil.ItemCallback<Venue> diffCallback, VenueSelectedListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_home, parent, false);

        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final Venue venue = getItem(position);
        if(venue != null){
            holder.bind(venue);
        }else{
            holder.clear();
        }
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private CardViewHomeBinding binding;

        public HomeViewHolder(CardViewHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(Venue venue){
            binding.name.setText(venue.getName());
            binding.address.setText(venue.location.address);
            binding.address.setMaxLines(2);
            Glide.with(binding.getRoot()).load(venue.url)
                    .placeholder(R.drawable.progress_bar)
                    .into(binding.mainImage);
            binding.cardView.setOnClickListener(view -> listener.onVenueSelected(venue));
        }

        public void clear(){

        }
    }

    public interface VenueSelectedListener{
        void onVenueSelected(Venue venue);
    }
}
