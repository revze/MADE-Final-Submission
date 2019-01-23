package io.revze.searchmovie.view.detail;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.revze.searchmovie.R;
import io.revze.searchmovie.db.DatabaseContract;
import io.revze.searchmovie.model.FavoriteMovie;
import io.revze.searchmovie.model.Genre;
import io.revze.searchmovie.model.MovieDetailResponse;
import io.revze.searchmovie.utils.GlideApp;
import io.revze.searchmovie.view.adapter.GenreAdapter;

import static io.revze.searchmovie.db.DatabaseContract.CONTENT_URI;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailView {

    private MovieDetailPresenter presenter;
    private Context context;
    public static final String ID = "id";
    public static final String TITLE = "title";
    private int id;
    private SwipeRefreshLayout swrDetail;
    private CircleImageView ivPoster;
    private TextView tvTitle, tvTagline, tvRating, tvDuration, tvLanguage,
            tvReleaseDate, tvOverview;
    private RecyclerView rvGenre;
    private GenreAdapter adapter;
    private List<Genre> genres = new ArrayList<>();
    private FavoriteMovie currentFavoriteMovie;
    private MenuItem menuFavorite;
    private Uri uri;
    private MovieDetailResponse response;
    private Cursor cursor;
    private final String POSTER = "poster";
    private final String TAGLINE = "tagline";
    private final String RATING = "rating";
    private final String DURATION = "duration";
    private final String LANGUAGE = "language";
    private final String RELEASE_DATE = "release_date";
    private final String OVERVIEW = "overview";
    private final String GENRE = "genre";
    private String posterUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        presenter = new MovieDetailPresenter();
        onAttachView();
        context = this;
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        if (intent != null) {
            id = intent.getIntExtra(ID, 0);
            actionBar.setTitle(intent.getStringExtra(TITLE));
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        swrDetail = findViewById(R.id.swr_detail);
        ivPoster = findViewById(R.id.iv_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvTagline = findViewById(R.id.tv_tagline);
        tvRating = findViewById(R.id.tv_rating);
        tvDuration = findViewById(R.id.tv_duration);
        tvLanguage = findViewById(R.id.tv_language);
        tvReleaseDate = findViewById(R.id.tv_release_date);
        tvOverview = findViewById(R.id.tv_overview);
        rvGenre = findViewById(R.id.rv_genre);
        adapter = new GenreAdapter();
        adapter.setGenres(genres);
        rvGenre.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvGenre.setAdapter(adapter);
        rvGenre.setNestedScrollingEnabled(false);

        uri = getIntent().getData();

        swrDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getMovieDetail(context, id);
            }
        });

        if (savedInstanceState != null) {
            posterUrl = savedInstanceState.getString(POSTER);
            List<Genre> genres = savedInstanceState.getParcelableArrayList(GENRE);

            MovieDetailResponse savedResponse = new MovieDetailResponse();
            savedResponse.setTitle(savedInstanceState.getString(TITLE));
            savedResponse.setPoster(posterUrl);
            savedResponse.setRating(savedInstanceState.getString(RATING));
            savedResponse.setDuration(savedInstanceState.getString(DURATION));
            savedResponse.setLanguage(savedInstanceState.getString(LANGUAGE));
            savedResponse.setReleaseDate(savedInstanceState.getString(RELEASE_DATE));
            savedResponse.setGenres(genres);
            savedResponse.setOverview(savedInstanceState.getString(OVERVIEW));
            savedResponse.setTagline(savedInstanceState.getString(TAGLINE));

            onSuccessGetDetail(savedResponse);
        } else {
            presenter.getMovieDetail(context, id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.favorite:
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        getContentResolver().delete(uri, null, null);
                        menuFavorite.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border));
                        showSnackbarMessage(getString(R.string.removed_from_favorite_message));
                    } else {
                        if (response != null) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DatabaseContract.FavoriteMovieColumns._ID, id);
                            contentValues.put(DatabaseContract.FavoriteMovieColumns.TITLE, response.getTitle());
                            contentValues.put(DatabaseContract.FavoriteMovieColumns.SHORT_DESCRIPTION, response.getOverview());
                            contentValues.put(DatabaseContract.FavoriteMovieColumns.POSTER, response.getPoster());
                            getContentResolver().insert(CONTENT_URI, contentValues);
                            menuFavorite.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_favorite_pink));
                            showSnackbarMessage(getString(R.string.added_to_favorite_message));
                        }
                    }
                    cursor.close();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onDestroy() {
        onDetachView();
        super.onDestroy();
    }

    @Override
    public void showLoader() {
        swrDetail.setRefreshing(true);
    }

    @Override
    public void hideLoader() {
        swrDetail.setRefreshing(false);
    }

    @Override
    public void onSuccessGetDetail(MovieDetailResponse response) {
        this.response = response;

        posterUrl = response.getPoster();
        GlideApp.with(context).load("https://image.tmdb.org/t/p/original/" + posterUrl).into(ivPoster);
        tvTitle.setText(response.getTitle());
        tvTagline.setText(response.getTagline());
        tvRating.setText(response.getRating());
        tvDuration.setText(response.getDuration());
        tvLanguage.setText(response.getLanguage());
        tvReleaseDate.setText(response.getReleaseDate());
        tvOverview.setText(response.getOverview());
        this.genres.clear();
        this.genres.addAll(response.getGenres());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailedGetDetail(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        menuFavorite = menu.findItem(R.id.favorite);
        cursor = getContentResolver().query(uri, null, null, null, null);

        menuFavorite.setVisible(true);
        if (cursor != null) {
            menuFavorite.setIcon(ContextCompat.getDrawable(context, cursor.moveToFirst() ? R.drawable.ic_favorite_pink : R.drawable.ic_favorite_border));
            cursor.close();
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(POSTER, posterUrl);
        outState.putString(TITLE, tvTitle.getText().toString());
        outState.putString(TAGLINE, tvTagline.getText().toString());
        outState.putString(RATING, tvRating.getText().toString());
        outState.putString(DURATION, tvDuration.getText().toString());
        outState.putString(LANGUAGE, tvLanguage.getText().toString());
        outState.putString(RELEASE_DATE, tvReleaseDate.getText().toString());
        outState.putString(OVERVIEW, tvOverview.getText().toString());
        outState.putParcelableArrayList(GENRE, (ArrayList) genres);
    }
}
