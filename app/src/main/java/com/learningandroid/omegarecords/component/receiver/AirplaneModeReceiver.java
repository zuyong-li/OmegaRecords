package com.learningandroid.omegarecords.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * a broadcast receiver to detect airplane mode change
 */
public class AirplaneModeReceiver extends BroadcastReceiver {

    private static final String TAG = "AIRPLANE MODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {
            Toast.makeText(context, "Airplane Mode is off", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "airplane mode off");
        } else {
            Toast.makeText(context, "Airplane Mode is on", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "airplane mode on");
        }
    }
}