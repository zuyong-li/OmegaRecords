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
    public static final String TIMER_CHANNEL_ID = "count up timer";

    public static final int REVISIT_NOTIFY_ID = 100;
    public static final int ALARM_NOTIFY_ID = 101;
    public static final int TIMER_NOTIFY_ID = 102;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(REVISIT_CHANNEL_ID,
                "revisit channel",
                "Channel for revisit reminder");
        createNotificationChannel(ALARM_CHANNEL_ID,
                "repeating alarm channel",
                "Channel for repeating alarms");
        createNotificationChannel(TIMER_CHANNEL_ID,
                "timer channel",
                "Channel for counting up timer");
    }

    /**
     * a helper method to create a notification channel with the given channelId, name, and description
     * all channels created by this method have the default importance
     */
    private void createNotificationChannel(final String channelId, String name, String description) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
