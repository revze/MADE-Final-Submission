package io.revze.favoritemovieapp.model;

import android.database.Cursor;

import io.revze.favoritemovieapp.db.DatabaseContract;

import static io.revze.favoritemovieapp.db.DatabaseContract.getColumnInt;
import static io.revze.favoritemovieapp.db.DatabaseContract.getColumnString;

public class FavoriteMovie {
    private int id;

    private String title;

    private String shortDescription;

    private String poster;

    public FavoriteMovie() {
    }

    public FavoriteMovie(Cursor cursor) {
        setId(getColumnInt(cursor, DatabaseContract.FavoriteMovieColumns._ID));
        setTitle(getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.TITLE));
        setShortDescription(getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.SHORT_DESCRIPTION));
        setPoster(getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.POSTER));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
