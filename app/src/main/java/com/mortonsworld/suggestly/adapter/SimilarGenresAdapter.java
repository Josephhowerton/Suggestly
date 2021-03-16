package com.mortonsworld.suggestly.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.mortonsworld.suggestly.R;
import com.mortonsworld.suggestly.databinding.CardViewCategoryBinding;

public class SimilarGenresAdapter extends PagedListAdapter<String, SimilarGenresAdapter.SimilarViewHolder> {

    private final GenreSelectedListener selectedListener;

    protected SimilarGenresAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, GenreSelectedListener selectedListener) {
        super(diffCallback);

        this.selectedListener = selectedListener;
    }

    @NonNull
    @Override
    public SimilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_category, parent, false);
        return new SimilarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarViewHolder holder, int position) {
        String genre = getItem(position);
        if(genre != null){
            holder.bind(genre);
        }else{
            holder.clear();
        }
    }

    public class SimilarViewHolder extends RecyclerView.ViewHolder{
        private final CardViewCategoryBinding binding;
        public SimilarViewHolder(@NonNull CardViewCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String genre){
            binding.name.setText(genre);
            binding.cardView.setOnClickListener(view -> selectedListener.onGenreSelected(genre));
        }

        public void clear(){}
    }

    public interface GenreSelectedListener{
        void onGenreSelected(String genre);
    }
}
