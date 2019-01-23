package io.revze.searchmovie.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.revze.searchmovie.db.DatabaseContract;

import static io.revze.searchmovie.db.DatabaseContract.getColumnInt;
import static io.revze.searchmovie.db.DatabaseContract.getColumnString;

public class FavoriteMovie {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("overview")
    @Expose
    private String shortDescription;

    @SerializedName("backdrop_path")
    @Expose
    private String poster;

    public FavoriteMovie() {}

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
