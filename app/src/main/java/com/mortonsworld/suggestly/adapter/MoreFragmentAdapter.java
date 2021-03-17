package com.mortonsworld.suggestly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.RowItemMoreBinding;
import com.mortonsworld.suggestly.interfaces.DetailsCallback;
import com.mortonsworld.suggestly.interfaces.Suggestion;
import com.mortonsworld.suggestly.model.foursquare.Venue;
import com.mortonsworld.suggestly.model.nyt.Book;

import java.util.List;

public class MoreFragmentAdapter extends RecyclerView.Adapter<MoreFragmentAdapter.MoreViewHolder> {
    private final Context context;
    private final List<Suggestion> suggestions;
    private final DetailsCallback listener;

    public MoreFragmentAdapter(Context context, List<Suggestion> suggestions, DetailsCallback detailsCallback){
        this.context = context;
        this.suggestions = suggestions;
        this.listener = detailsCallback;
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



    public class MoreViewHolder extends RecyclerView.ViewHolder{
        RowItemMoreBinding binding;
        public MoreViewHolder(@NonNull RowItemMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Suggestion suggestion){
            switch (suggestion.getSuggestionType()){
                case FOURSQUARE_VENUE:
                    bindVenue((Venue) suggestion);
                    break;
                case BOOK:
                    bindBook((Book) suggestion);
                    break;
            }
        }

        private void bindVenue(Venue venue){
            binding.name.setText(venue.getName());
            binding.address.setText(venue.location.getFormattedAddress());
            //todo add icon image from url
            binding.getRoot().setOnClickListener(view -> listener.onSuggestionDetailsListener(venue));
        }

        private void bindBook(Book book){
            binding.name.setText(book.getTitleWithAuthor());
            binding.address.setText(book.getDescription());
            Glide.with(binding.getRoot()).load(book.getBookImage()).placeholder(R.drawable.progress_bar).into(binding.icon);
            binding.getRoot().setOnClickListener(view -> listener.onSuggestionDetailsListener(book));
        }
    }
}
