/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.michaelbraha.popular_movies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Michael on 7/14/2016.
 * Most of this is taken directly from Udacity Sunshine 2 app project.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate.
    void deleteTheDatabase() {
        mContext.deleteDatabase(FavoriteDBHelper.DATABASE_NAME);
    }

    // This is called before each test, ensuring the DB is clean.
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // Build a HashSet of all the table names we wish to look for.
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FavoriteContract.FavoriteEntry.TABLE_NAME);

        mContext.deleteDatabase(FavoriteDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoriteDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Have we created the table we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // Verify that the table has been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while ( c.moveToNext() );

        // If this fails, it means the database doesn't contain the favorite entry
        assertTrue("Errot: Database was created without the favorite entry", tableNameHashSet.isEmpty());

        // Now, does the table contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FavoriteContract.FavoriteEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means we were unable to query the database for table information",
                c.moveToFirst());

        // Build a HastSet of all the column names we want to look for
        final HashSet<String> favoriteColumnHashSet = new HashSet<String>();
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_IMAGE);
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW);
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_VOTE);
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE);
        favoriteColumnHashSet.add(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            favoriteColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // If this fails, it means that your DB doesn't contain all of the required favorite entry columns
        assertTrue("Error: The databse doesn't contain all of the required favorite entry columns",
                favoriteColumnHashSet.isEmpty());
        db.close();
        c.close();
    }

    // Here is where we test that we can insert and query the database.

    public void testFavoriteTable() {
        // First step: Get reference to writable database
        FavoriteDBHelper dbHelper = new FavoriteDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createFavoriteValues();

        // Insert ContentValues into database and get a row ID back
        long rowID = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back
        assertTrue(rowID != -1);

        // Query the database and receive a Cursor back
        Cursor c = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME, null, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        assertTrue("Error: No records returned from favorite query", c.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Favorite Query validation failed", c, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from favorite query", c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }


}
