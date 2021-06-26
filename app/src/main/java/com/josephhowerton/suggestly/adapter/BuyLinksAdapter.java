package com.josephhowerton.suggestly.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.josephhowerton.suggestly.R;
import com.josephhowerton.suggestly.databinding.CardViewCategoryBinding;
import com.josephhowerton.suggestly.app.model.nyt.BuyLink;

import java.util.List;

public class BuyLinksAdapter extends RecyclerView.Adapter<BuyLinksAdapter.BuyLinksViewHolder> {

    private final BuySelectedBookListener selectedListener;
    private final List<BuyLink> buyLinks;

    
    public BuyLinksAdapter(@NonNull List<BuyLink> buyLinks, BuySelectedBookListener selectedListener) {
        this.selectedListener = selectedListener;
        this.buyLinks = buyLinks;
    }

    @NonNull
    @Override
    public BuyLinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_view_category, parent, false);
        return new BuyLinksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyLinksViewHolder holder, int position) {
        BuyLink buyLink = buyLinks.get(position);
        if(buyLink != null){
            holder.bind(buyLink);
        }
    }

    @Override
    public int getItemCount() {
        return buyLinks.size();
    }

    public class BuyLinksViewHolder extends RecyclerView.ViewHolder{
        private final CardViewCategoryBinding binding;
        public BuyLinksViewHolder(@NonNull CardViewCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BuyLink buyLink){
            binding.name.setText(buyLink.name);
            binding.icon.setImageResource(R.drawable.ic_baseline_web_white_24);
            binding.cardView.setOnClickListener(view -> selectedListener.onBuySelectedBook(buyLink.url));
        }
    }

    public interface BuySelectedBookListener{
        void onBuySelectedBook(String url);
    }
}
