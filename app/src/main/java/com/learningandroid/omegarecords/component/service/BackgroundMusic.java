package com.learningandroid.omegarecords.component.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import com.learningandroid.omegarecords.R;

/**
 * A started service to play a background music for this app
 * the music is downloaded from
 * https://www.fesliyanstudios.com/royalty-free-music/downloads-c/peaceful-and-relaxing-music/22
 */
public class BackgroundMusic extends Service {

    public static MediaPlayer mediaPlayer;

    public BackgroundMusic() {}

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.quiet);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
