package com.learningandroid.omegarecords.component.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.learningandroid.omegarecords.OmegaRecordsApp;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.db.UserDatabase;
import com.learningandroid.omegarecords.component.receiver.NotificationReceiver;
import com.learningandroid.omegarecords.component.service.BackgroundMusic;
import com.learningandroid.omegarecords.component.service.TimerService;
import com.learningandroid.omegarecords.viewmodel.SettingsViewModel;

/**
 * SignInActivity allows end users to sign in this app using their Gmail accounts
 * When logged out, this activity will also show up to allow the end user to sign in again
 * deep linking enabled, this app can be accessed via https://www.learningandroid.com
 */
public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SIGN IN";
    private static final int SIGN_IN = 300;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // create a google signin client which provides signin intent later
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // if this intent is accompanied with message sign out,
        // stop background music, then sign out current account
        // it cancels the repeating notifications and timer
        // it removes all the notifications and clear local database
        if(getIntent().hasExtra("sign_out")) {
            Intent backgroundMusicIntent = new Intent(this, BackgroundMusic.class);
            stopService(backgroundMusicIntent);
            Intent timerIntent = new Intent(this, TimerService.class);
            stopService(timerIntent);

            googleSignInClient.signOut();
            cancelRepeatingAlarms();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(OmegaRecordsApp.REVISIT_NOTIFY_ID);
            manager.cancel(OmegaRecordsApp.ALARM_NOTIFY_ID);
            UserDatabase.getInstance(this).clearAllTables();
        }

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        findViewById(R.id.sign_in_button).setOnClickListener((View view) -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN);
        });

        Uri uri = getIntent().getData();
        if(uri != null) {
            Toast.makeText(this, "launched from url: " + uri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * it cancels the current repeating notification
     */
    private void cancelRepeatingAlarms() {
        Intent receiverIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(alarmManager != null) {
            Log.i(TAG, "cancel repeating alarm");
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * it starts a repeating notification every minute
     */
    private void startRepeatingAlarms() {
        Intent receiverIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000,
                60 * 1000, pendingIntent);
        Log.i(TAG, "started repeating alarm");
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * update the user interface
     * first check the background music setting and turn it on if true
     * then start an AppInfoActivity, a repeating notification and a timer
     */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
            if(settingsViewModel.loadBackgroundMusicSetting()){
                Intent backgroundMusicIntent = new Intent(this, BackgroundMusic.class);
                startService(backgroundMusicIntent);
            }

            startRepeatingAlarms();
            Intent timerIntent = new Intent(this, TimerService.class);
            ContextCompat.startForegroundService(this, timerIntent);
            Intent appInfoIntent = new Intent(this, AppInfoActivity.class);
            startActivity(appInfoIntent);
        }
    }

    /**
     * handles the result of signin activity
     * if the end user successfully signed in, invoke updateUI to redirect to AppInfoActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) { // result for sign in intent
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUI(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}

