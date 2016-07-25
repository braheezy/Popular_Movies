package com.michaelbraha.popular_movies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Michael on 7/16/2016.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_FAVORITE_DIR = FavoriteContract.FavoriteEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = FavoriteProvider.buildUriMatcher();

        assertEquals("Error: The FAVORITE URI was matched incorrectly.",
                testMatcher.match(TEST_FAVORITE_DIR), FavoriteProvider.FAVORITE);
    }

}
