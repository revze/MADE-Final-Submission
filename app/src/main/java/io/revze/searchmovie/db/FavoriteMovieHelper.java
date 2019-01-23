package io.revze.searchmovie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import io.revze.searchmovie.model.FavoriteMovie;

public class FavoriteMovieHelper {
    private static String DATABASE_TABLE = DatabaseContract.TABLE_FAVORITE_MOVIE;
    private Context context;
    private DatabaseHelper databaseHelper;

    private SQLiteDatabase database;

    public FavoriteMovieHelper(Context context) {
        this.context = context;
    }

    public FavoriteMovieHelper open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public ArrayList<FavoriteMovie> query() {
        ArrayList<FavoriteMovie> arrayList = new ArrayList<FavoriteMovie>();
        Cursor cursor = database.query(DATABASE_TABLE, null, null, null, null, null, DatabaseContract.FavoriteMovieColumns._ID + " DESC", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                FavoriteMovie favoriteMovie = new FavoriteMovie();
                favoriteMovie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteMovieColumns._ID)));
                favoriteMovie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteMovieColumns.TITLE)));
                favoriteMovie.setShortDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteMovieColumns.SHORT_DESCRIPTION)));
                favoriteMovie.setPoster(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.FavoriteMovieColumns.POSTER)));

                arrayList.add(favoriteMovie);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();

        return arrayList;
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_TABLE, null, DatabaseContract.FavoriteMovieColumns._ID + " = ?", new String[]{id}, null,
                null, null, null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_TABLE, null, null, null, null, null,
                DatabaseContract.FavoriteMovieColumns._ID + " DESC");
    }

    public long insertProvider(ContentValues contentValues) {
        return database.insert(DATABASE_TABLE, null, contentValues);
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_TABLE, DatabaseContract.FavoriteMovieColumns._ID + " = ?", new String[]{id});
    }
}
