package io.revze.searchmovie.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.revze.searchmovie.R;
import io.revze.searchmovie.service.DailyReminderService;
import io.revze.searchmovie.service.ReleaseTodayReminderService;
import io.revze.searchmovie.view.adapter.CustomViewPagerAdapter;
import io.revze.searchmovie.view.movie.favorite.FavoriteMovieFragment;
import io.revze.searchmovie.view.movie.nowplaying.NowPlayingMovieMovieFragment;
import io.revze.searchmovie.view.movie.search.SearchMovieFragment;
import io.revze.searchmovie.view.movie.upcoming.UpcomingMovieMovieFragment;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private DailyReminderService dailyReminderService;
    private ReleaseTodayReminderService releaseTodayReminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        final ViewPager viewPager = findViewById(R.id.view_pager);
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        Menu bottomNavigationMenu = bottomNavigation.getMenu();
        final MenuItem menuNowPlay = bottomNavigationMenu.findItem(R.id.now_playing_movie);
        final MenuItem menuUpPlay = bottomNavigationMenu.findItem(R.id.up_playing_movie);
        final MenuItem menuSearch = bottomNavigationMenu.findItem(R.id.search_movie);
        final MenuItem menuFavorite = bottomNavigationMenu.findItem(R.id.favorite_movie);
        dailyReminderService = new DailyReminderService(context);
        releaseTodayReminderService = new ReleaseTodayReminderService(context);

        viewPager.setOffscreenPageLimit(4);
        adapter.addFragment(new NowPlayingMovieMovieFragment(), getString(R.string.movie_now_playing_title));
        adapter.addFragment(new UpcomingMovieMovieFragment(), getString(R.string.movie_up_playing_title));
        adapter.addFragment(new SearchMovieFragment(), getString(R.string.movie_search_movie_title));
        adapter.addFragment(new FavoriteMovieFragment(), getString(R.string.movie_favorite_movie_title));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        menuNowPlay.setChecked(true);
                        break;
                    case 1:
                        menuUpPlay.setChecked(true);
                        break;
                    case 2:
                        menuSearch.setChecked(true);
                        break;
                    case 3:
                        menuFavorite.setChecked(true);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.now_playing_movie:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.up_playing_movie:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.search_movie:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.favorite_movie:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        dailyReminderService.setRepeatingAlarm(context);
        releaseTodayReminderService.setRepeatingAlarm(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language_setting:
                Intent langSettingIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(langSettingIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
