package com.learningandroid.omegarecords.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.learningandroid.omegarecords.OmegaRecordsApp;
import com.learningandroid.omegarecords.R;


/**
 * a broadcast receiver to display a notification
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, OmegaRecordsApp.ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_channel)
                .setContentTitle("repeated notification")
                .setContentText("This is a repeated notification sent every minute");

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(OmegaRecordsApp.ALARM_NOTIFY_ID, builder.build());
    }
}