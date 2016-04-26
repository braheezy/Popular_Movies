package com.michaelbraha.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailFragment extends Fragment {

    RecyclerView rvTrailers;
    Trailer trailer = new Trailer();
    ArrayList<Trailer> trailers;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration screenConfig = getResources().getConfiguration();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        MovieItem receivedMovieItem = getActivity().getIntent().getParcelableExtra("MovieItemParcel");
        new TrailerTask().execute(receivedMovieItem);

        ((TextView) rootView.findViewById(R.id.movie_title_textview)).setText(receivedMovieItem.getTitle());

        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_imageview);
        Picasso.with(getContext()).load(receivedMovieItem.getImage()).into(posterImageView);
        if (screenConfig.orientation == 2) {
            posterImageView.getLayoutParams().width = ((int) getResources().getDimension(R.dimen.detail_image_width)) / 2;
            posterImageView.getLayoutParams().height = ((int) getResources().getDimension(R.dimen.detail_image_height)) / 2;
        }

        ((TextView) rootView.findViewById(R.id.year_release_date_textview)).setText(receivedMovieItem.getReleaseDate());

        String voteText = receivedMovieItem.getVote() + getString(R.string.over_10);
        ((TextView) rootView.findViewById(R.id.vote_textview)).setText(voteText);

        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_textview);
        overviewTextView.setText(receivedMovieItem.getOverview());
        overviewTextView.setMovementMethod(new ScrollingMovementMethod());

        rvTrailers = (RecyclerView) rootView.findViewById(R.id.trailer_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(1);
        this.rvTrailers.setLayoutManager(llm);
        return rootView;
    }

    public class TrailerTask extends AsyncTask<MovieItem, Void, String[]> {
        private final String LOG_TAG = TrailerTask.class.getSimpleName();

        protected String[] doInBackground(MovieItem... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String id = BuildConfig.THE_MOVIE_DATABASE_API_KEY;
            String movieId = params[0].getMovieId();
            String apiResult;

            try {
                String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
                String VIDEOS = "videos";
                String ID_PARAM = "api_key";

                Uri builtUri;
                builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", id)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.d(this.LOG_TAG, url.toString());

                // Create the request to TheMovieDatabase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    apiResult = null;
                }

                apiResult = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            Log.d(LOG_TAG, apiResult);
            return createYoutubeURLsFromJSON(apiResult);
        }

        private String[] createYoutubeURLsFromJSON(String result) {
            String TMDB_RESULTS = "results";
            String TMDB_KEY = "key";
            String[] videoKeysArray = new String[0];
            try {

                JSONArray resultsArray = new JSONObject(result).getJSONArray(TMDB_RESULTS);
                int numVideos = resultsArray.length();
                videoKeysArray = new String[numVideos];

                for (int i = 0; i < numVideos; i++) {
                    videoKeysArray[i] = resultsArray.getJSONObject(i).getString(TMDB_KEY);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (String s : videoKeysArray) {
                Log.d(LOG_TAG, "Key for video: " + s);
            }

            return createYoutubeURLs(videoKeysArray);
        }

        private String[] createYoutubeURLs(String[] trailerKeys) {
            int len = trailerKeys.length;
            String BASE_YOUTUBE_URL = "http://www.youtube.com/watch?v=";
            String[] urlStrings = new String[len];
            for (int i = 0; i < len; i++) {
                urlStrings[i] = BASE_YOUTUBE_URL + trailerKeys[i];
            }
            return urlStrings;
        }

        protected void onPostExecute(String[] youtubeURLs) {
            if (youtubeURLs != null) {
                for (String s : youtubeURLs){
                    Log.d("Youtube URLs: ", s);
                }

                Context context = getContext();
                trailers = Trailer.createTrailerList(youtubeURLs.length);

                trailer.setURLs(youtubeURLs);
                rvTrailers.setAdapter(new TrailerAdapter(trailers));
                for (int i = 0; i < youtubeURLs.length; i++) {
                    trailers.get(i).setIndividualURL(trailer.getSpecificURL(i));
                }
                rvTrailers.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                            public void onItemClick(View view, int position) {
                                boolean isIntentSafe = false;
                                Intent videoIntent = new Intent("android.intent.action.VIEW", Uri
                                        .parse(trailers.get(position).getIndividualURL()));
                                if (getContext().getPackageManager().queryIntentActivities(videoIntent, 0).size() > 0) {
                                    isIntentSafe = true;
                                }
                                if (isIntentSafe) {
                                    startActivity(videoIntent);
                                } else {
                                    Log.d("LOOOOK", "Intent isn't safe");
                                }
                            }
                        })
                );
            }
        }
    }
}