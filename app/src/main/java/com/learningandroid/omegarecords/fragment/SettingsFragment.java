package com.learningandroid.omegarecords.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.domain.Settings;
import com.learningandroid.omegarecords.service.BackgroundMusic;
import com.learningandroid.omegarecords.utils.ActivityUtils;

import org.jetbrains.annotations.NotNull;

/**
 * a simple settings fragment to turn on and turn off the background music
 */
public class SettingsFragment extends Fragment {

    private final String SETTING_KEY = "settingsKey";
    private final String FILENAME_KEY = "fileName";

    private Settings settings;
    private String fileName;

    public SettingsFragment() { }
    public SettingsFragment(Settings settings, String fileName) {
        this.settings = settings;
        this.fileName = fileName;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        String settingsJson = ActivityUtils.getGsonParser().toJson(settings);
        outState.putString(SETTING_KEY, settingsJson);
        outState.putString(FILENAME_KEY, fileName);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            String settingsString = savedInstanceState.getString(SETTING_KEY);
            settings = ActivityUtils.getGsonParser().fromJson(settingsString, Settings.class);
            fileName = savedInstanceState.getString(FILENAME_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // use a switch to turn on/off the background music
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = view.findViewById(R.id.setting_background_music_switch);
        aSwitch.setChecked(settings.getBackgroundMusicOn());
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) { // turn on background music
                Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
                requireActivity().startService(backgroundMusicIntent);
                settings.setBackgroundMusicOn(true);
                Log.d("background music", "start background music");
            } else { // turn off background music
                Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
                requireActivity().stopService(backgroundMusicIntent);
                settings.setBackgroundMusicOn(false);
                Log.d("background music", "stop background music");
            }
        });

        // apply changes, it saves the setting details to internal storage
        // it also removes the setting fragment when OK button is clicked
        Button applySettings = view.findViewById(R.id.setting_applay);
        applySettings.setOnClickListener((View v) ->{
            Log.d("setting apply", "apply button is clicked");
            Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.setting_fragment_container);
            if(fragment != null) {
                Log.d("remove fragment", "remove the setting fragment");
                requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        String settingsText = ActivityUtils.getGsonParser().toJson(settings);
        ActivityUtils.saveData(requireContext(), settingsText, fileName);
        super.onStop();
    }

}