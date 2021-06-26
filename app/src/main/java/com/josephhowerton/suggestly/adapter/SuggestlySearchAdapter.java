package com.josephhowerton.suggestly.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.databinding.RowItemMoreBinding;
import com.josephhowerton.suggestly.app.model.relations.SearchTuple;

public class SuggestlySearchAdapter extends PagedListAdapter<SearchTuple, SuggestlySearchAdapter.SuggestlyViewHolder> {

    private final SearchResultListener listener;

    public SuggestlySearchAdapter(SearchResultListener listener) {
        super(VENUE_CALLBACK);
        this.listener = listener;
    }


    @NonNull
    @Override
    public SuggestlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemMoreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_item_more, parent, false);
        return new SuggestlyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestlyViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class SuggestlyViewHolder extends RecyclerView.ViewHolder{
        RowItemMoreBinding binding;
        public SuggestlyViewHolder(@NonNull RowItemMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SearchTuple searchResult){
            if(searchResult != null){
                bindVenue(searchResult);
            }
        }

        private void bindVenue(SearchTuple searchResult){
            binding.name.setText(searchResult.name);
            binding.address.setText(searchResult.category_name);
            binding.getRoot().setOnClickListener(view -> listener.onResultSelected(searchResult));
            Glide.with(binding.getRoot()).load(searchResult.getIconWithBGUrl(64))
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.icon);
        }
    }

    private static final DiffUtil.ItemCallback<SearchTuple> VENUE_CALLBACK = new DiffUtil.ItemCallback<SearchTuple>() {
        @Override
        public boolean areItemsTheSame(@NonNull SearchTuple oldItem, @NonNull SearchTuple newItem) {
            return oldItem.venueId.equals(newItem.venueId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchTuple oldItem, @NonNull SearchTuple newItem) {
            return oldItem.venueId.equals(newItem.venueId);
        }
    };

    public interface SearchResultListener{
        void onResultSelected(SearchTuple searchResult);
    }
}

