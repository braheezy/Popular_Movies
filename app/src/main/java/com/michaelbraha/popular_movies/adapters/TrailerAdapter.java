package com.michaelbraha.popular_movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.michaelbraha.popular_movies.R;
import com.michaelbraha.popular_movies.objects.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private List<Trailer> mTrailers;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageButton playImageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            playImageButton = (ImageButton) itemView.findViewById(R.id.play_image_view);
        }
    }

    public TrailerAdapter(List<Trailer> trailers) {
        mTrailers = trailers;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trailerView = inflater.inflate(R.layout.trailer_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(trailerView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.nameTextView.setText(mTrailers.get(position).getName());
        viewHolder.playImageButton.setImageResource(R.drawable.play);
    }

    // Return the total count of items
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        }
        return mTrailers.size();
    }
}