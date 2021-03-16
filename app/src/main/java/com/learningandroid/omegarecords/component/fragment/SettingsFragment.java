package com.learningandroid.omegarecords.component.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.component.service.BackgroundMusic;
import com.learningandroid.omegarecords.viewmodel.SettingsViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * a simple settings fragment to turn on and turn off the background music
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = "SETTINGS FRAGMENT";

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // use a switch to turn on/off the background music
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = view.findViewById(R.id.setting_background_music_switch);
        aSwitch.setChecked(settingsViewModel.loadBackgroundMusicSetting());
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
            if (isChecked) {
                requireActivity().startService(backgroundMusicIntent);
                settingsViewModel.saveBackgroundMusicSetting(true);
                Log.i(TAG, "start background music");
            } else {
                requireActivity().stopService(backgroundMusicIntent);
                settingsViewModel.saveBackgroundMusicSetting(false);
                Log.i(TAG, "stop background music");
            }
        });

        // remove the setting fragment when OK button is clicked
        Button applySettings = view.findViewById(R.id.setting_applay);
        applySettings.setOnClickListener((View v) -> {
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.setting_fragment_container);
            if (fragment != null) {
                Log.i(TAG, "apply settings and remove the setting fragment");
                requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        return view;
    }
}