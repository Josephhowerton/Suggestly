package com.josephhowerton.suggestly.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.callbacks.SaveCallback;
import com.josephhowerton.suggestly.databinding.CardViewHomeBinding;
import com.josephhowerton.suggestly.model.nyt.Book;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeBookAdapter extends PagedListAdapter<Book, HomeBookAdapter.HomeViewHolder> {

    private final BookSelectedListener listener;
    private final SaveCallback saveVenueListener;
    private final List<Book> saved;
    public HomeBookAdapter(@NonNull DiffUtil.ItemCallback<Book> diffCallback, BookSelectedListener listener, SaveCallback saveVenueListener) {
        super(diffCallback);
        this.listener = listener;
        this.saveVenueListener = saveVenueListener;
        this.saved = new ArrayList<>();
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
            holder.bindSavedImage(book);
            holder.bind(book);
            holder.bindSavedAnimation(book);
        }
    }

    public void setSavedList(List<Book> saved){
        this.saved.clear();
        this.saved.addAll(saved);
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
            Glide.with(binding.getRoot())
                    .load(book.getBookImage())
                    .placeholder(R.drawable.glide_progress_bar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainImage);
        }

        public void bindSavedImage(@NotNull Book book){
            for(Book temp: saved){
                if(temp.getId().equals(book.getId())){
                    binding.saveImage.setChecked(true);
                }
            }
        }

        public void bindSavedAnimation(Book book) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
            binding.saveImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                compoundButton.startAnimation(scaleAnimation);
                saveVenueListener.onSuggestionSaved(book, isChecked);
            });
        }
    }

    public interface BookSelectedListener{
        void onBookSelected(Book book);
    }
}
