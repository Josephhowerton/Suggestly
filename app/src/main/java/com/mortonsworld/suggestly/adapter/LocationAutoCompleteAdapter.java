package com.mortonsworld.suggestly.adapter;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.mortonsworld.suggestly.R;

import java.util.ArrayList;
import java.util.List;

public class LocationAutoCompleteAdapter extends RecyclerView.Adapter<LocationAutoCompleteAdapter.LocationAutoCompleteViewHolder> {
    private final List<AutocompletePrediction> predictions;
    private final LocationSearchAdapterListener listener;

    public LocationAutoCompleteAdapter(LocationSearchAdapterListener listener){
        this.predictions = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationAutoCompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_location_search, parent, false);

        return new LocationAutoCompleteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAutoCompleteViewHolder holder, int position) {
        String address = predictions.get(position).getFullText(new StyleSpan(Typeface.BOLD)).toString();
        holder.address.setText(address);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setPredictions(List<AutocompletePrediction> predictions){
        this.predictions.clear();
        this.predictions.addAll(predictions);
        notifyDataSetChanged();
    }

    public class LocationAutoCompleteViewHolder extends RecyclerView.ViewHolder{
        private final TextView address;
        public LocationAutoCompleteViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            itemView.setOnClickListener(view -> listener.onRowItemSelected(predictions.get(getAdapterPosition()).getPrimaryText(new StyleSpan(Typeface.BOLD)).toString()));
        }
    }

    public interface LocationSearchAdapterListener {
        void onRowItemSelected(String address);
    }
}
