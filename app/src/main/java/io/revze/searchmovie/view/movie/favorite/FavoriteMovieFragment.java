package io.revze.searchmovie.view.movie.favorite;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.revze.searchmovie.R;
import io.revze.searchmovie.db.FavoriteMovieHelper;
import io.revze.searchmovie.view.adapter.FavoriteMovieAdapter;

import static io.revze.searchmovie.db.DatabaseContract.CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMovieFragment extends Fragment {

    private Context context;
    private RecyclerView rvFavoriteMovie;
    private Cursor favoriteMovies;
    private FavoriteMovieAdapter adapter;
    private FavoriteMovieHelper favoriteMovieHelper;
    private LinearLayout layoutLoader;
    private TextView tvEmptyMovie;

    public FavoriteMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = requireActivity();
        layoutLoader = view.findViewById(R.id.layout_loader);
        tvEmptyMovie = view.findViewById(R.id.tv_empty_movie);
        rvFavoriteMovie = view.findViewById(R.id.rv_favorite_movie);
        rvFavoriteMovie.setLayoutManager(new LinearLayoutManager(context));
        rvFavoriteMovie.setHasFixedSize(true);

        favoriteMovieHelper = new FavoriteMovieHelper(context);
        favoriteMovieHelper.open();

        adapter = new FavoriteMovieAdapter(context);
        adapter.setFavoriteMovies(favoriteMovies);
        rvFavoriteMovie.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new FavoriteMovieAsync().execute();
    }

    private class FavoriteMovieAsync extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            hideLoader();

            favoriteMovies = cursor;
            adapter.setFavoriteMovies(favoriteMovies);
            adapter.notifyDataSetChanged();

            if (favoriteMovies.getCount() == 0) {
                tvEmptyMovie.setVisibility(View.VISIBLE);
                rvFavoriteMovie.setVisibility(View.GONE);
            } else {
                tvEmptyMovie.setVisibility(View.GONE);
                rvFavoriteMovie.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showLoader() {
        layoutLoader.setVisibility(View.VISIBLE);
        tvEmptyMovie.setVisibility(View.GONE);
        rvFavoriteMovie.setVisibility(View.GONE);
    }

    private void hideLoader() {
        layoutLoader.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
