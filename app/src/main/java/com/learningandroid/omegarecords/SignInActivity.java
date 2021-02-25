package com.learningandroid.omegarecords;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.learningandroid.omegarecords.domain.Settings;
import com.learningandroid.omegarecords.receiver.NotificationReceiver;
import com.learningandroid.omegarecords.service.BackgroundMusic;
import com.learningandroid.omegarecords.utils.ActivityUtils;

/**
 * SignInActivity allows end users to sign in this app using their Gmail accounts
 * When logged out, this activity will also show up to allow the end user to sign in again
 */
public class SignInActivity extends AppCompatActivity {

    private static final int SIGN_IN = 102;
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
        // it also cancels the repeating notifications
        if(getIntent().hasExtra("sign_out")) {
            Intent backgroundMusicIntent = new Intent(this, BackgroundMusic.class);
            stopService(backgroundMusicIntent);
            googleSignInClient.signOut();
            cancelRepeatingAlarms();
        }

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        findViewById(R.id.sign_in_button).setOnClickListener((View view) -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN);
        });
    }

    /**
     * it cancels the current repeating notification
     */
    private void cancelRepeatingAlarms() {
        Intent receiverIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(alarmManager != null) {
            Log.d("repeating alarms", "canceled in log out");
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
        Log.d("repeating alarms", "started in app info activity");
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * update the user interface once this activity is started
     * first check the background music setting and turn it on if true
     * then start an AppInfoActivity and a repeating notification
     */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // check whether the background music should be on
            ActivityUtils<Settings> utils = new ActivityUtils<>();
            String fileName = account.getEmail() + ".settings.txt";
            if(utils.loadData(this, fileName, new Settings()).getBackgroundMusicOn()) {
                Intent backgroundMusicIntent = new Intent(this, BackgroundMusic.class);
                startService(backgroundMusicIntent);
            }

            startRepeatingAlarms();
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

