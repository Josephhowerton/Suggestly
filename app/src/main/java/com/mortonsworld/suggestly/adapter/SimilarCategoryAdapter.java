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
import com.mortonsworld.suggestly.databinding.CardViewCategoryBinding;
import com.mortonsworld.suggestly.model.foursquare.Category;

public class SimilarCategoryAdapter extends PagedListAdapter<Category, SimilarCategoryAdapter.SimilarViewHolder> {

    private final CategorySelectedListener selectedListener;

    public SimilarCategoryAdapter(@NonNull DiffUtil.ItemCallback<Category> diffCallback, CategorySelectedListener selectedListener) {
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
        Category category = getItem(position);
        if(category != null){
            holder.bind(category);
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

        public void bind(Category category){
            binding.name.setText(category.name);
            binding.cardView.setOnClickListener(view -> selectedListener.onCategorySelected(category));
            Glide.with(binding.getRoot()).load(category.icon.prefix + "48X48" + category.icon.suffix).into(binding.icon);
        }

        public void clear(){}
    }

    public interface CategorySelectedListener{
        void onCategorySelected(Category category);
    }
}
