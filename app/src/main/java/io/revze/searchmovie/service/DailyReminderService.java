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

import java.util.Calendar;
import java.util.Locale;

import io.revze.searchmovie.R;
import io.revze.searchmovie.view.main.MainActivity;

public class DailyReminderService extends BroadcastReceiver {
    private final String CHANNEL_ID = "daily_reminder";
    private final String CHANNEL_NAME = "Daily Reminder";
    private final int DAILY_REMINDER = 1;
    private SharedPreferences sharedPreferences;
    private final String PREFERENCE_NAME = "daily_reminder_preference";
    private final String IS_SERVICE_STARTED = "is_service_started";

    public DailyReminderService() {
    }

    public DailyReminderService(Context context) {
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
    public void onReceive(Context context, Intent intent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_pink)
                .setContentText(context.getString(R.string.daily_reminder_message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder.setChannelId(CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification = notificationBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(0, notification);
        }
    }

    public void setRepeatingAlarm(Context context) {
        if (!isServiceStarted()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, DailyReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER, intent, 0);

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);

            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                setServiceStarted(true);
            }
        }
    }
}
