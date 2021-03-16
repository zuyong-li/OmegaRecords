package com.learningandroid.omegarecords.component.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.learningandroid.omegarecords.OmegaRecordsApp;
import com.learningandroid.omegarecords.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * a started service to display the time elapsed since the user has logged in
 */
public class TimerService extends Service {

    Timer timer = new Timer();
    Integer time = 0;
    TimerTask timerTask;
    NotificationCompat.Builder builder;
    NotificationManagerCompat manager;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new NotificationCompat.Builder(this, OmegaRecordsApp.TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_channel)
                .setContentTitle("Timer")
                .setContentText("The time you spent on this app is " + getTime());
        manager = NotificationManagerCompat.from(this);
    }

    /**
     * convert the current time in seconds to the form of HH:MM:SS
     */
    @SuppressLint("DefaultLocale")
    private String getTime() {
        int hours = (time % 86400) / 3600;
        int minutes = ((time % 86400) % 3600) / 60;
        int seconds = ((time % 86400) % 3600) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(OmegaRecordsApp.TIMER_NOTIFY_ID, builder.build());
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                builder.setContentText("The time you spent on this app is " + getTime());
                manager.notify(OmegaRecordsApp.TIMER_NOTIFY_ID, builder.build());
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timerTask = null;
        stopForeground(true);
        super.onDestroy();
    }
}