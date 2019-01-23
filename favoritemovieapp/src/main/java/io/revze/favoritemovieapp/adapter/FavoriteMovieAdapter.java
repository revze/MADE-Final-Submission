package io.revze.favoritemovieapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.revze.favoritemovieapp.R;
import io.revze.favoritemovieapp.db.DatabaseContract;
import io.revze.favoritemovieapp.utils.GlideApp;

import static io.revze.favoritemovieapp.db.DatabaseContract.getColumnString;

public class FavoriteMovieAdapter extends CursorAdapter {
    public FavoriteMovieAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_row_movie, parent, false);
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            CardView cvMovie = view.findViewById(R.id.cv_movie);
            ImageView ivPoster = view.findViewById(R.id.iv_poster);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            TextView tvShortDescription = view.findViewById(R.id.tv_short_desc);

            GlideApp.with(context).load("https://image.tmdb.org/t/p/original/" + getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.POSTER)).into(ivPoster);
            tvTitle.setText(getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.TITLE));
            tvShortDescription.setText(getColumnString(cursor, DatabaseContract.FavoriteMovieColumns.SHORT_DESCRIPTION));
        }
    }
}
