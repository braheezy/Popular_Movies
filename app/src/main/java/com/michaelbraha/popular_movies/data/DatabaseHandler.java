package com.michaelbraha.popular_movies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.michaelbraha.popular_movies.objects.MovieItem;

import java.util.ArrayList;

/**
 * Created by Michael on 7/26/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "favoritesManager";

    private static final String TABLE_FAVORITES = "favorites";

    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_VOTE = "vote";
    private static final String KEY_DATE = "date";
    private static final String KEY_MOVIE_ID = "movie_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
            + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_IMAGE + " TEXT NOT NULL, "
                + KEY_TITLE + " TEXT NOT NULL, " + KEY_OVERVIEW + " TEXT NOT NULL, "
                + KEY_VOTE + " TEXT NOT NULL, " + KEY_DATE + " TEXT NOT NULL, "
                + KEY_MOVIE_ID + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        onCreate(db);
    }

    /**
     * CRUD Operations
     */
    public void addFavorite(MovieItem fav) {
        if (checkIfDataExists(fav.getTitle())) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, fav.getImage());
        values.put(KEY_TITLE, fav.getTitle());
        values.put(KEY_OVERVIEW, fav.getOverview());
        values.put(KEY_VOTE, fav.getVote());
        values.put(KEY_DATE, fav.getReleaseDate());
        values.put(KEY_MOVIE_ID, fav.getMovieId());

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public boolean checkIfDataExists(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[] {KEY_TITLE},
                KEY_TITLE + " = ?",
                new String[] {title},
                null,null,null,null);

        if(cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        else {
            cursor.close();
            db.close();
            return false;
        }
    }

    public MovieItem getFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[]{ KEY_ID, KEY_IMAGE, KEY_TITLE, KEY_OVERVIEW, KEY_VOTE, KEY_DATE, KEY_MOVIE_ID},
                KEY_ID + "=?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        MovieItem fav = new MovieItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), //image
                cursor.getString(2), //title
                cursor.getString(3), //overview
                cursor.getString(4), //vote
                cursor.getString(5), //date
                cursor.getString(6)); //movie id

        cursor.close();
        db.close();

        return fav;
    }

    public ArrayList<MovieItem> getAllFavorites() {
        ArrayList<MovieItem> favoriteList = new ArrayList<MovieItem>();

        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MovieItem fav = new MovieItem();
                fav.setID(Integer.parseInt(cursor.getString(0)));
                fav.setImage(cursor.getString(1));
                fav.setTitle(cursor.getString(2));
                fav.setOverview(cursor.getString(3));
                fav.setVote(cursor.getString(4));
                fav.setReleaseDate(cursor.getString(5));
                fav.setMovieId(cursor.getString(6));

                favoriteList.add(fav);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteList;
    }

    public int updateFavorite (MovieItem fav) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, fav.getImage());
        values.put(KEY_TITLE, fav.getTitle());
        values.put(KEY_OVERVIEW, fav.getOverview());
        values.put(KEY_VOTE, fav.getVote());
        values.put(KEY_DATE, fav.getReleaseDate());
        values.put(KEY_MOVIE_ID, fav.getMovieId());

        int rowUpdated = db.update(TABLE_FAVORITES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(fav.getID()) });

        db.close();

        return rowUpdated;
    }

    public void deleteFavorite (String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_TITLE + " = ?",
                new String[] { title });
        db.close();
    }

    public int getFavoriteCount() {
        String countQuery = "SELECT * FROM " + TABLE_FAVORITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }
}
