package com.michaelbraha.popular_movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelbraha.popular_movies.R;
import com.michaelbraha.popular_movies.objects.Review;

import java.util.ArrayList;

/**
 * Created by Michael on 5/2/2016.
 */
public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> mReviews;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView reviewTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.author_textview);
            reviewTextView = (TextView) itemView.findViewById(R.id.review_textview);
        }
    }

    public ReviewAdapter(ArrayList<Review> reviews) {
        mReviews = reviews;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View trailerView = inflater.inflate(R.layout.review_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(trailerView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder viewHolder, int position) {
        if (mReviews != null){
            String authorText = mReviews.get(position).getAuthorArray().get(position);
            viewHolder.authorTextView.setText(authorText);
            String reviewText = mReviews.get(position).getReviewArray().get(position);
            viewHolder.reviewTextView.setText(reviewText);
        }
    }

    // Return the total count of items
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.size();
    }
}
