package com.michaelbraha.popular_movies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.michaelbraha.popular_movies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Michael on 7/14/2016.
 * Most of this is taken directly from Udacity Sunshine 2 app project.
 */
public class TestUtilities extends AndroidTestCase {


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
            "' did not match the expected value '" +
            expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    // Some default values to test.

    static ContentValues createFavoriteValues () {
        ContentValues favoriteValues = new ContentValues();
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_IMAGE, "http://image.tmdb.org/t/p/w185/tcqb9NHdw9SWs2a88KCDD4V8sVR.jpg");
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "Avatar");
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, "In the 22nd century, a paraplegic Marine is dispatched to the moon Pandora on a unique mission, but becomes torn between following orders and protecting an alien civilization.");
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE, "7.1");
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, "2009");
        favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, "19995");

        return favoriteValues;
    }

    static long insertFavoriteValues (Context context) {
        // insert our test records into the db
        FavoriteDBHelper dbHelper = new FavoriteDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createFavoriteValues();

        long favoriteRowId;
        favoriteRowId = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, testValues);

        // Verify we get a row back
        assertTrue("Errpr: Failure to insert North Pole Location Values", favoriteRowId != -1);

        return favoriteRowId;
    }

    /* The functions inside of TestProvider use this utility class to rest the ContentObserver callbacks
    using the PollingCheck class grabbed from Android CTS tests.

    Note that this only tests that the onChange function is called; it does not test that the correct Uri
    is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
        HandlerThread ht = new HandlerThread("ContentObserverThread");
        ht.start();
        return new TestContentObserver(ht);
    }

    private TestContentObserver(HandlerThread ht) {
        super(new Handler(ht.getLooper()));
        mHT = ht;
    }

    // Earlier versions of Android call this.
    @Override
        public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
        public void onChange(boolean selfChange, Uri uri) {
        mContentChanged = true;
    }

     public void waitForNotificationOrFail() {
         // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
         // It's useful to look at the Android CTS source for ideas on how to test your Android
         // applications.  The reason that PollingCheck works is that, by default, the JUnit
         // testing framework is not running on the main Android application thread.
         new PollingCheck(5000) {
             @Override
             protected boolean check() {
                 return mContentChanged;
             }
         }.run();
         mHT.quit();
     }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
