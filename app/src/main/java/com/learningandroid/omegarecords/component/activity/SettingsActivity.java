package com.learningandroid.omegarecords.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.component.service.BackgroundMusic;
import com.learningandroid.omegarecords.storage.entity.User;
import com.learningandroid.omegarecords.utils.GsonProvider;
import com.learningandroid.omegarecords.viewmodel.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends NavigationPane {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        onCreateDrawer(findViewById(R.id.drawer_layout));

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private static final String URL = "https://jsonplaceholder.typicode.com/users";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat switchPreferenceCompat = findPreference("backgroundMusic");
            switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
                Intent backgroundMusicIntent = new Intent(getContext(), BackgroundMusic.class);
                if ((boolean) newValue) {
                    requireActivity().startService(backgroundMusicIntent);
                } else {
                    requireActivity().stopService(backgroundMusicIntent);
                }
                return true;
            });

            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

            Preference removeAllPreference = findPreference("deleteAll");
            removeAllPreference.setOnPreferenceClickListener(preference -> {
                userViewModel.deleteAllUsers();
                return true;
            });

            Preference syncPreference = findPreference("sync");
            syncPreference.setOnPreferenceClickListener(preference -> {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(URL).build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d(TAG, "failed to execute request of fetch users from url");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        User[] users = GsonProvider.getInstance()
                                .fromJson(Objects.requireNonNull(response.body()).string(), User[].class);
                        if (response.isSuccessful()) {
                            userViewModel.insertUsers(users);
                        }
                    }
                });
                return true;
            });
        }
    }
}