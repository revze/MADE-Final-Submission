package io.revze.favoritemovieapp.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_FAVORITE_MOVIE = "favorite_movie";

    public static final class FavoriteMovieColumns implements BaseColumns {
        public static String TITLE = "title";
        public static String SHORT_DESCRIPTION = "short_description";
        public static String POSTER = "poster";
    }

    public static final String AUTHORITY = "io.revze.searchmovie";

    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHORITY)
            .appendPath(TABLE_FAVORITE_MOVIE)
            .build();

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
}
