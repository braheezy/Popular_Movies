package com.michaelbraha.popular_movies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Michael on 7/16/2016.
 *  Most of this is taken directly from Udacity Sunshine 2 app project.
 */
public class TestFavoriteContract extends AndroidTestCase {

    // Intentionally include a slash to make sure Uri is getting quoted correctly
    private static final long TEST_FAVORITE = 999;

    public void testBuildFavorite() {
        Uri favoriteUri = FavoriteContract.FavoriteEntry.buildFavoriteUri(TEST_FAVORITE);

        assertNotNull("Error: Null Uri returned. You must fill-in buildFavoriteUri in FavoriteContract",
                favoriteUri);
        assertEquals("Error: Favorite not properly appended to the end of the Uri",
                String.valueOf(TEST_FAVORITE), favoriteUri.getLastPathSegment());
        assertEquals("Error: Favorite uri doesn't match the expected result",
                favoriteUri.toString(),
                "content://com.michaelbraha.popular_movies.data/favorite/999");
    }
}
