package com.learningandroid.omegarecords.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.learningandroid.omegarecords.viewmodel.repository.SettingsRepository;

/**
 * a view model that provides loading and saving app settings
 * app settings are stored as key-value pairs in shared preferences
 */
public class SettingsViewModel extends AndroidViewModel {

    private final SettingsRepository repository = new SettingsRepository();
    private final Application application;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public boolean loadBackgroundMusicSetting() {
        return repository.loadBackgroundMusicSetting(application);
    }

    public void saveBackgroundMusicSetting(boolean isMusicOn) {
        repository.saveBackgroundMusicSetting(application, isMusicOn);
    }
}
