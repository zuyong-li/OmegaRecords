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

import com.learningandroid.omegarecords.NavigationPane;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.domain.Settings;
import com.learningandroid.omegarecords.service.BackgroundMusic;
import com.learningandroid.omegarecords.utils.GsonParser;

import org.jetbrains.annotations.NotNull;

/**
 * a simple settings fragment to turn on and turn off the background music
 */
public class SettingsFragment extends Fragment {

    private Settings settings;
    private final String SETTING_KEY = "settingsKey";

    public SettingsFragment() { }
    public SettingsFragment(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        String settinsJson = GsonParser.getGsonParser().toJson(settings);
        outState.putString(SETTING_KEY, settinsJson);
    }


    public void setMusicOn(Boolean isMusicON) {
        settings.setBackgroundMusicOn(isMusicON);
        NavigationPane.setSettings(settings);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            String settingsString = savedInstanceState.getString(SETTING_KEY);
            settings = GsonParser.getGsonParser().fromJson(settingsString, Settings.class);
        }

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // use a switch to turn on/off the background music
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = view.findViewById(R.id.setting_background_music_switch);
        aSwitch.setChecked(settings.getBackgroundMusicOn());
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) { // turn on background music
                Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
                requireActivity().startService(backgroundMusicIntent);
                setMusicOn(true);
                Log.d("background music", "start background music");
            } else { // turn off background music
                Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
                requireActivity().stopService(backgroundMusicIntent);
                setMusicOn(false);
                Log.d("background music", "stop background music");
            }
        });

        // apply changes, it has no effects on the settings
        // it simply remove the setting fragment when OK button is clicked
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
}