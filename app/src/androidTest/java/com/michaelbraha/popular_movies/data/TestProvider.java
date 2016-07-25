package com.michaelbraha.popular_movies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Michael on 7/16/2016.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

     /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Favorite table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

     /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // FavoriteProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                FavoriteProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: FavoriteProvider registered with authority: " + providerInfo.authority +
            " instead of authority: " + FavoriteContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FavoriteContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly
            assertTrue("Error: FavoriteProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
        /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
        */
    public void testGetType() {
        String type = mContext.getContentResolver().getType(FavoriteContract.FavoriteEntry.CONTENT_URI);
        assertEquals("Error: the FavoriteEntry CONTENT_URI should return FavoriteEntry.CONTENT_URI",
                FavoriteContract.FavoriteEntry.CONTENT_TYPE, type);
    }
    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    public void testBasicFavoriteQuery() {
        // insert our test records into the db
        FavoriteDBHelper dbHelper = new FavoriteDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createFavoriteValues();
        long favoriteRowId = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert FavoriteEntry into the Database", favoriteRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor favoriteCursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicFavoriteQuery", favoriteCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Favorite Query did not properly set NotificationUri",
                    favoriteCursor.getNotificationUri(), FavoriteContract.FavoriteEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data.
     */
    public void testUpdateFavorite() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFavoriteValues();

        Uri favoriteUri = mContext.getContentResolver().
                insert(FavoriteContract.FavoriteEntry.CONTENT_URI, values);
        long favoriteRowId = ContentUris.parseId(favoriteUri);

        // Verify we got a row back
        assertTrue(favoriteRowId != -1);
        Log.d(LOG_TAG, "New row id: " + favoriteRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(FavoriteContract.FavoriteEntry._ID, favoriteRowId);
        updatedValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "Batman");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor favoriteCursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        favoriteCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                updatedValues,
                FavoriteContract.FavoriteEntry._ID + "=?",
                new String[] { Long.toString(favoriteRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        favoriteCursor.unregisterContentObserver(tco);
        favoriteCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                FavoriteContract.FavoriteEntry._ID + " = " + favoriteRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateFavorite. Error validating favorite entry update.",
                cursor, updatedValues);
        cursor.close();
    }

    // Make sure we can still delete after adding/updating stuff
    public void testInsertReadProvider() {

        ContentValues testValues = TestUtilities.createFavoriteValues();

        // Register a content observer for our insert. This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FavoriteContract.FavoriteEntry.CONTENT_URI,
                true, tco);
        Uri favoriteUri = mContext.getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, testValues);

        // Did our content observer get called? If this fails, insert favorite isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long favoriteRowId = ContentUris.parseId(favoriteUri);

        // Verify we got a row back
        assertTrue(favoriteRowId != -1);

        // Data's inserted. IN THEORY. Now pull some out and verify it made the round trip

        // A cursor is the primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating FavoriteEntry",
                cursor, testValues);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our favorite delete.
        TestUtilities.TestContentObserver favoriteObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                FavoriteContract.FavoriteEntry.CONTENT_URI, true, favoriteObserver);

        deleteAllRecordsFromProvider();

        favoriteObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(favoriteObserver);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertFavoriteValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues favoriteValues = new ContentValues();
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_IMAGE, "http://image.tmdb.org/t/p/w185/tcqb9NHdw9SWs2a88KCDD4V8sVR.jpg");
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, "Avatar");
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, "In the 22nd century, a paraplegic Marine is dispatched to the moon Pandora on a unique mission, but becomes torn between following orders and protecting an alien civilization.");
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE, "7.1" + i);
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, "2009" + i);
            favoriteValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, "19995" + i);
            returnContentValues[i] = favoriteValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        // First we can bulkInsert some favorites.
        ContentValues[] bulkInsertContentValues = createBulkInsertFavoriteValues();

        // Register a content observer for our bulk insert
        TestUtilities.TestContentObserver favoriteObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                FavoriteContract.FavoriteEntry.CONTENT_URI, true, favoriteObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                FavoriteContract.FavoriteEntry.CONTENT_URI, bulkInsertContentValues);

        favoriteObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(favoriteObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is the primary interface to query results
        Cursor cursor = mContext.getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // We should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // And let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating FavoriteEntry " + 1,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
