package com.michaelbraha.popular_movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Michael on 7/7/2016.
 * Most of this is taken directly from Udacity Sunshine 2 app project.
 */
public class FavoriteContract {

    // The authority used for the provider. Conveniently named after our unique package.
    public static final String CONTENT_AUTHORITY = "com.michaelbraha.popular_movies.data";

    // The base URI used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Valid paths to look at data, appended to the base content URI
    public static final String PATH_FAVORITE = "favorite";

    /* Inner class defining table contents for favorite table.  */
    public static final class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorite";

        // Image url stored as text
        public static final String COLUMN_IMAGE = "image";

        // Movie title stored as text
        public static final String COLUMN_TITLE = "title";

        // Summary of movie stored as text
        public static final String COLUMN_OVERVIEW = "overview";

        // Rating of movie, stored as text
        public static final String COLUMN_VOTE = "vote";

        // Release date for movie, given as year only, stored as text
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Unique id for movie, for other API calls, stored as text
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
