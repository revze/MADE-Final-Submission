package io.revze.searchmovie.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.revze.searchmovie.BuildConfig;
import io.revze.searchmovie.R;
import io.revze.searchmovie.api.ApiClient;
import io.revze.searchmovie.api.ApiService;
import io.revze.searchmovie.model.Movie;
import io.revze.searchmovie.model.NowPlayingMovieResponse;
import io.revze.searchmovie.view.main.MainActivity;

public class ReleaseTodayReminderService extends BroadcastReceiver {
    private final String CHANNEL_ID = "release_today_reminder";
    private final String CHANNEL_NAME = "Release Today Reminder";
    private final int RELEASE_TODAY_REMINDER = 2;
    private SharedPreferences sharedPreferences;
    private final String PREFERENCE_NAME = "release_today_reminder_preference";
    private final String IS_SERVICE_STARTED = "is_service_started";
    private ApiService apiService = ApiClient.getClient();

    public ReleaseTodayReminderService() {
    }

    public ReleaseTodayReminderService(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void setServiceStarted(boolean isStarted) {
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putBoolean(IS_SERVICE_STARTED, isStarted);
        preferenceEditor.apply();
    }

    private boolean isServiceStarted() {
        return sharedPreferences.getBoolean(IS_SERVICE_STARTED, false);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        apiService.getNowPlayingMovie(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_API_LANG)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<NowPlayingMovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NowPlayingMovieResponse nowPlayingMovieResponse) {
                        List<Movie> movies = nowPlayingMovieResponse.getMovies();
                        for (int i = 0; i < movies.size(); i++) {
                            Movie movie = movies.get(i);

                            if (isToday(movie.getReleaseDate())) {
                                generateNotification(context, movie.getId(), movie.getTitle());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setRepeatingAlarm(Context context) {
        if (!isServiceStarted()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReleaseTodayReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RELEASE_TODAY_REMINDER, intent, 0);

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);

            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                setServiceStarted(true);
            }
        }
    }

    private void generateNotification(Context context, int movieId, String movieTitle) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle(movieTitle)
                .setContentText(movieTitle + " " + context.getString(R.string.release_today_info))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationBuilder.setChannelId(CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification = notificationBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(movieId, notification);
        }
    }

    private boolean isToday(String date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return date.equals(dateFormatter.format(calendar.getTime()));
    }
}
