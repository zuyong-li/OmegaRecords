package com.learningandroid.omegarecords.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.learningandroid.omegarecords.OmegaRecordsApp;
import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.domain.LoggedInUser;
import com.learningandroid.omegarecords.domain.Settings;
import com.learningandroid.omegarecords.fragment.SettingsFragment;
import com.learningandroid.omegarecords.receiver.AirplaneModeReceiver;
import com.learningandroid.omegarecords.utils.ActivityUtils;


import java.util.Objects;

/**
 * this class provides the method for creating and setting the menu
 * it also stores the useful information needed for the whole app, USERS, ME, FILENAME
 * it also ensures that rotation functions as expected by overriding onSaveInstanceState and onRestoreInstanceState
 */
public class NavigationPane extends AppCompatActivity {


    public static final int CAMERA_PERMISSION_REQUEST_CODE = 103;
    public static final int CAMERA_INTENT_REQUEST_CODE = 104;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 105;

    GoogleSignInAccount account;
    ActionBarDrawerToggle actionBarDrawerToggle;
    AirplaneModeReceiver airplaneModeReceiver = new AirplaneModeReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = GoogleSignIn.getLastSignedInAccount(this);

        registerReceiver(airplaneModeReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * create and setup the menu
     */
    @SuppressLint("NonConstantResourceId")
    public void onCreateDrawer(DrawerLayout drawerLayout) {
        // call setDisplayHomeAsUpEnabled first to avoid warning:
        // DrawerToggle may not show up because NavigationIcon is not visible
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigation_pane);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // set up the name and email fields in the header
        if(account != null) {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name))
                    .setText(account.getDisplayName());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email))
                    .setText(account.getEmail());
        }

        // link corresponding activities to each menu option
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();

            switch(menuItem.getItemId()) {
                case R.id.app_info:
                    Intent infoIntent = new Intent(this, AppInfoActivity.class);
                    startActivity(infoIntent);
                    break;
                case R.id.edit_profile:
                    Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                    startActivity(editProfileIntent);
                    break;
                case R.id.view_users:
                    Intent viewUsersIntent = new Intent(this, ViewUsersActivity.class);
                    startActivity(viewUsersIntent);
                    break;
                case R.id.settings:
                    String fileName = account.getEmail() + ".settings.txt";
                    ActivityUtils<Settings> utils = new ActivityUtils<>();
                    Settings settings = utils.loadData(this, fileName, new Settings());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_fragment_container, new SettingsFragment(settings, fileName), null)
                            .commit();
                    break;
                case R.id.logout:
                    Intent signOutIntent = new Intent(this, SignInActivity.class);
                    signOutIntent.putExtra("sign_out", true);
                    startActivity(signOutIntent);
                    break;
                default:
                    break;
            }

            return true;
        });
    }

    /**
     * when the activity goes to background mode, i.e, when the home button is pressed
     * send a notification of "don't forget about LOGGED_IN_USER"
     * once the notification is tapped, redirect the activity back
     * ONLY IF the end user has not logged out yet
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, this.getIntent(), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, OmegaRecordsApp.REVISIT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_channel)
                .setContentTitle("visit again")
                .setContentText("don't forget about me, tap to revisit")
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(OmegaRecordsApp.REVISIT_NOTIFY_ID, builder.build());
    }

    /**
     * load the information of the loggedInUser
     * if there is no saved data, create an new LoggedInUser Object with only name and email
     */
    public LoggedInUser loadLoggedInUser() {
        if(account != null) {
            ActivityUtils<LoggedInUser> utils = new ActivityUtils<>();
            LoggedInUser user = new LoggedInUser();
            user.setEmail(account.getEmail());
            user.setName(account.getDisplayName());
            return utils.loadData(this, account.getEmail() + ".txt", user);
        } else {
            return null;
        }
    }

    /**
     * a helper method to request runtime permission
     * message: a string to show when shouldShowRequestRationale returns true
     * permission: the name of the permission to request
     */
    protected void requestPermission(String message, @NonNull String permission, final int request_code) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("Allow", (dialog, which) ->
                            ActivityCompat.requestPermissions(this, new String[] {permission}, request_code))
                    .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {permission}, request_code);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(airplaneModeReceiver);
        } catch (Exception e) {
            Log.d("unregister receiver", e.getMessage());
        }
    }
}