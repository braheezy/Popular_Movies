package com.michaelbraha.popular_movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.michaelbraha.popular_movies.adapters.GridViewAdapter;
import com.michaelbraha.popular_movies.objects.MovieItem;

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

/**
 * Created by Michael on 2/28/2016.
 */
public class MovieFragment extends Fragment {

    public GridViewAdapter mGridViewAdapter;
    public ArrayList<MovieItem> mGridData;
    private MovieItem movieItem;


    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("items")){
            mGridData = new ArrayList<MovieItem>();
        }
        else {
            mGridData = savedInstanceState.getParcelableArrayList("items");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("items", mGridData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mGridData != null){
            mGridData.clear();
        }
        FetchMovieTask fetchMovie = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMethod = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_top_rated));
        fetchMovie.execute(sortMethod);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridViewAdapter = new GridViewAdapter(getContext(), mGridData);
        gridView.setAdapter(mGridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){

                MovieItem movieItemClicked = mGridViewAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("MovieItemParcel", movieItemClicked);
                startActivity(intent);
            }
        });

        return rootView;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == R.id.action_favorite){
//            Log.d("Menu", "Favorite selected");
//            if(mGridData != null) {
//                mGridData.clear();
//            }
//            mGridData = readDatabase();
//            mGridViewAdapter.setGridData(mGridData);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    public ArrayList<MovieItem> readDatabase() {
//        String image;
//        String title;
//        String overview;
//        String vote;
//        String date;
//        String movieId;
//
//        MovieItem builtFromDatabaseMovieItem = new MovieItem();
//        ArrayList<MovieItem> gridData = new ArrayList<MovieItem>();
//
//        Cursor favoriteCursor = getContext().getContentResolver().query(
//                FavoriteContract.FavoriteEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//
//        try {
//            while (favoriteCursor.moveToNext()) {
//                int imageColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_IMAGE);
//                image = favoriteCursor.getString(imageColumn);
//                Log.d("Read Database check", image);
//
//                int titleColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
//                title = favoriteCursor.getString(titleColumn);
//                Log.d("Read Database check", title);
//
//                int overviewColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW);
//                overview = favoriteCursor.getString(overviewColumn);
//                Log.d("Read Database check", overview);
//
//                int voteColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE);
//                vote = favoriteCursor.getString(voteColumn);
//                Log.d("Read Database check", vote);
//
//                int dateColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE);
//                date = favoriteCursor.getString(dateColumn);
//                Log.d("Read Database check", date);
//
//                int movieIdColumn = favoriteCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);
//                movieId = favoriteCursor.getString(movieIdColumn);
//                Log.d("Read Database check", movieId);
//
//                builtFromDatabaseMovieItem.setEntireMovieItem(image, title, overview, vote, date, movieId);
//                gridData.add(builtFromDatabaseMovieItem);
//            }
//        } finally {
//            favoriteCursor.close();
//        }
//
//        return gridData;
//
//    }



    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String formatReleaseDate(String fullReleaseDate){
            String year;
            if(fullReleaseDate.length() == 0){
                year = "No release year found";
            } else {year = fullReleaseDate.substring(0,4);}
            return year;
        }

        private String formatPosterPath(String path){
            String basePath = "http://image.tmdb.org/t/p/";
            String imageSize = "w185";
            String fullPath = basePath + imageSize + path;
            return fullPath;
        }

        private String[] getMovieDataFromJSON(String movieStr) throws JSONException {

            // The names of all the objects that need to be obtained.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_TITLE = "title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_MOVIE_ID = "id";

            JSONObject movieJSON = new JSONObject(movieStr);
            JSONArray movieArray = movieJSON.getJSONArray(TMDB_RESULTS);

            int numMovies = movieArray.length();

            String[] resultStrs = new String[numMovies];
            for(int i = 0; i < movieArray.length(); i++) {
                String posterPath;
                String title;
                String overview;
                String releaseDate;
                String votes;
                String voteAverage;
                String movieId;

                // Get JSON object for representing the movie
                JSONObject movieObject = movieArray.getJSONObject(i);

                //All information is directly in movieObject, so just pull them out
                posterPath = movieObject.getString(TMDB_POSTER_PATH);
                title = movieObject.getString(TMDB_TITLE);
                overview = movieObject.getString(TMDB_OVERVIEW);
                releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
                votes = movieObject.getString(TMDB_VOTE_AVERAGE);
                movieId = movieObject.getString(TMDB_MOVIE_ID);

                releaseDate = formatReleaseDate(releaseDate);
                posterPath = formatPosterPath(posterPath);
                voteAverage = String.valueOf(votes);

                resultStrs[i] = posterPath + "*" + title + "*" + overview + "*"
                        + releaseDate + "*" + voteAverage + "*" + movieId;
            }
            for (String s : resultStrs){
                Log.d(LOG_TAG, s);
            }

            return resultStrs;
        }


        @Override
        public String[] doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }

            // These two need to be declared outside the try/catch block so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String apiResult;
            String sort = params[0];
            String id = BuildConfig.THE_MOVIE_DATABASE_API_KEY;
            String sortFilter = getString(R.string.pref_sort_min_vote);

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_PARAM = "sort_by";
                final String SORT_FILTER = getString(R.string.pref_sort_filter);
                final String ID_PARAM = "api_key";

                Uri builtUri;

                if (sort == getString(R.string.pref_sort_top_rated)){
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, sort)
                            .appendQueryParameter(SORT_FILTER, sortFilter)
                            .appendQueryParameter(ID_PARAM, id)
                            .build();
                }else {
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_PARAM, sort)
                            .appendQueryParameter(ID_PARAM, id)
                            .build();
                }

                URL url = new URL(builtUri.toString());

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
                while((line = reader.readLine()) != null){
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

            try {
                Log.d(LOG_TAG, apiResult);
                return getMovieDataFromJSON(apiResult);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing movie list
            return null;
        }



        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                for (String s : result){
                    MovieItem movieItem = new MovieItem(s);
                    mGridData.add(movieItem);
                }
                mGridViewAdapter.setGridData(mGridData);
            }
        }
    }
}
