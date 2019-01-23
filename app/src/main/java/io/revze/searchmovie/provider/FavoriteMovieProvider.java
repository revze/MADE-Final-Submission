package io.revze.searchmovie.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.revze.searchmovie.db.FavoriteMovieHelper;

import static io.revze.searchmovie.db.DatabaseContract.AUTHORITY;
import static io.revze.searchmovie.db.DatabaseContract.CONTENT_URI;
import static io.revze.searchmovie.db.DatabaseContract.TABLE_FAVORITE_MOVIE;

public class FavoriteMovieProvider extends ContentProvider {
    private static final int FAVORITE_MOVIE = 1;
    private static final int FAVORITE_MOVIE_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_FAVORITE_MOVIE, FAVORITE_MOVIE);
        uriMatcher.addURI(AUTHORITY, TABLE_FAVORITE_MOVIE + "/#", FAVORITE_MOVIE_ID);
    }

    private FavoriteMovieHelper favoriteMovieHelper;

    @Override
    public boolean onCreate() {
        favoriteMovieHelper = new FavoriteMovieHelper(getContext());
        favoriteMovieHelper.open();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                cursor = favoriteMovieHelper.queryProvider();
                break;
            case FAVORITE_MOVIE_ID:
                cursor = favoriteMovieHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long added;

        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                added = favoriteMovieHelper.insertProvider(contentValues);
                break;
            default:
                added = 0;
                break;
        }

        if (added > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE_ID:
                deleted = favoriteMovieHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }
}
