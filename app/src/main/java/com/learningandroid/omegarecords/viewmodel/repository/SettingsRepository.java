package com.learningandroid.omegarecords.viewmodel.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.learningandroid.omegarecords.R;

public class SettingsRepository {

    private SharedPreferences sharedPreferences;

    public SharedPreferences createSharedPreferences(@NonNull Context context) {
        if(sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    /**
     * check the background music setting in the shared preferences
     * by default, the background music is off
     */
    public boolean loadBackgroundMusicSetting(@NonNull Context context) {
        return createSharedPreferences(context).getBoolean(
                context.getString(R.string.preference_file_bkgndmusic_key), false);
    }

    /**
     * save the background music setting isMusicOn to the shared preferences
     */
    public void saveBackgroundMusicSetting(@NonNull  Context context, boolean isMusicOn) {
        createSharedPreferences(context).edit()
                .putBoolean(context.getString(R.string.preference_file_bkgndmusic_key), isMusicOn)
                .apply();
    }
}
