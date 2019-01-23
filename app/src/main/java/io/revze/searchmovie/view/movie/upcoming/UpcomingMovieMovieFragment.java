package io.revze.searchmovie.view.movie.upcoming;


import android.content.Context;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.revze.searchmovie.R;
import io.revze.searchmovie.model.Movie;
import io.revze.searchmovie.view.adapter.MovieAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingMovieMovieFragment extends Fragment implements UpcomingMovieView {

    private UpcomingMoviePresenter presenter;
    private Context context;
    private RecyclerView rvMovie;
    private List<Movie> movies = new ArrayList<>();
    private MovieAdapter adapter;
    private LinearLayout layoutLoader;
    private static final String LIST = "list";
    private static final String FAILED_LOAD_LIST = "failed_load_list";
    private boolean isFailedLoadList = false;

    public UpcomingMovieMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new UpcomingMoviePresenter();
        onAttachView();
        context = requireActivity();

        layoutLoader = view.findViewById(R.id.layout_loader);
        rvMovie = view.findViewById(R.id.rv_movie);
        adapter = new MovieAdapter(context);

        rvMovie.setLayoutManager(new LinearLayoutManager(context));
        adapter.setMovies(movies);
        rvMovie.setAdapter(adapter);

        if (savedInstanceState != null) {
            isFailedLoadList = savedInstanceState.getBoolean(FAILED_LOAD_LIST);
            List<Movie> savedMovies = savedInstanceState.getParcelableArrayList(LIST);

            if (isFailedLoadList) presenter.getMovie(context);
            else onSuccessGetMovie(savedMovies);
        } else {
            presenter.getMovie(context);
        }
    }

    @Override
    public void onAttachView() {
        presenter.onAttach(this);
    }

    @Override
    public void onDetachView() {
        presenter.onDetach();
    }

    @Override
    public void onDestroy() {
        onDetachView();
        super.onDestroy();
    }

    @Override
    public void onFailedGetMovie(String message) {
        isFailedLoadList = true;

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessGetMovie(List<Movie> movies) {
        isFailedLoadList = false;

        rvMovie.setVisibility(View.VISIBLE);
        this.movies.clear();
        this.movies.addAll(movies);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoader() {
        rvMovie.setVisibility(View.GONE);
        layoutLoader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        rvMovie.setVisibility(View.VISIBLE);
        layoutLoader.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(LIST, (ArrayList) movies);
        outState.putBoolean(FAILED_LOAD_LIST, isFailedLoadList);
        super.onSaveInstanceState(outState);
    }
}
