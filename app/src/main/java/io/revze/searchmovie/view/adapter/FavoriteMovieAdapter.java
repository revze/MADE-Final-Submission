package io.revze.searchmovie.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.revze.searchmovie.R;
import io.revze.searchmovie.model.FavoriteMovie;
import io.revze.searchmovie.utils.GlideApp;
import io.revze.searchmovie.view.detail.MovieDetailActivity;

import static io.revze.searchmovie.db.DatabaseContract.CONTENT_URI;

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieAdapter.MovieViewHolder> {
    private Context context;
    private Cursor favoriteMovies;

    public void setFavoriteMovies(Cursor favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public FavoriteMovieAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_movie, parent, false);
        return new MovieViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final FavoriteMovie favoriteMovie = getItem(position);

        GlideApp.with(context).load("https://image.tmdb.org/t/p/original/" + favoriteMovie.getPoster()).into(holder.ivPoster);
        holder.tvTitle.setText(favoriteMovie.getTitle());
        holder.tvShortDescription.setText(favoriteMovie.getShortDescription());
        holder.cvMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(CONTENT_URI + "/" + favoriteMovie.getId());
                Intent detailIntent = new Intent(context, MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.ID, favoriteMovie.getId());
                detailIntent.putExtra(MovieDetailActivity.TITLE, favoriteMovie.getTitle());
                detailIntent.setData(uri);
                context.startActivity(detailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (favoriteMovies == null) return 0;
        return favoriteMovies.getCount();
    }

    private FavoriteMovie getItem(int position) {
        if (!favoriteMovies.moveToPosition(position)) {
            throw new IllegalStateException("Position Invalid");
        }

        return new FavoriteMovie(favoriteMovies);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cvMovie;
        ImageView ivPoster;
        TextView tvTitle, tvShortDescription;

        public MovieViewHolder(View itemView) {
            super(itemView);
            cvMovie = itemView.findViewById(R.id.cv_movie);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvShortDescription = itemView.findViewById(R.id.tv_short_desc);
        }
    }
}