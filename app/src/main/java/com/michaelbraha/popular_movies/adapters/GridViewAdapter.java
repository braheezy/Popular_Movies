package com.michaelbraha.popular_movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.michaelbraha.popular_movies.objects.MovieItem;
import com.michaelbraha.popular_movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Michael on 3/4/2016.
 */
public class GridViewAdapter extends ArrayAdapter<MovieItem> {

    private Context mContext;
    private ArrayList<MovieItem> gridData = new ArrayList<MovieItem>();
    private int layoutResourceId;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<MovieItem> items) {
        super(context, 0, items);
        this.mContext = context;
        this.gridData = items;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public MovieItem getItem(int position) {
        return super.getItem(position);
    }

    public void setGridData(ArrayList<MovieItem> imageUrl){
        this.gridData = imageUrl;
        notifyDataSetChanged();
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;

        if (view == null) {
            view = inflater.inflate(R.layout.gridview_item, parent, false);
            view.setTag(R.id.gridview_item, view.findViewById(R.id.gridview_item));
        }

        imageView = (ImageView) view.getTag(R.id.gridview_item);
        MovieItem item = gridData.get(position);
        Picasso.with(mContext).load(item.getImage()).into(imageView);

        return view;
    }
}
