package com.learningandroid.omegarecords;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * it is a singleton class with the following functionality
 * it creates the notification channels
 */
public class OmegaRecordsApp extends Application {
    public static final String REVISIT_CHANNEL_ID = "visit again";
    public static final String ALARM_CHANNEL_ID = "repeated notification";
    public static final int REVISIT_NOTIFY_ID = 100;
    public static final int ALARM_NOTIFY_ID = 101;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(REVISIT_CHANNEL_ID,
                    "revisit channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Channel for revisit reminder");

            NotificationChannel channel2 = new NotificationChannel(ALARM_CHANNEL_ID,
                    "repeated channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("Channel for repeated notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
