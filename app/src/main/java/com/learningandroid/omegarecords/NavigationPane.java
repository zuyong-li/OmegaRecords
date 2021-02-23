package com.learningandroid.omegarecords;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.learningandroid.omegarecords.domain.LoggedInUser;
import com.learningandroid.omegarecords.domain.Settings;
import com.learningandroid.omegarecords.domain.User;
import com.learningandroid.omegarecords.fragment.SettingsFragment;
import com.learningandroid.omegarecords.utils.GsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static com.learningandroid.omegarecords.OmegaRecordsApp.CHANNEL_ID;

/**
 * this class provides the method for creating and setting the menu
 * it also stores the useful information needed for the whole app, USERS, ME, FILENAME
 * it also ensures that rotation functions as expected by overriding onSaveInstanceState and onRestoreInstanceState
 */
public class NavigationPane extends AppCompatActivity {

    public static final LoggedInUser LOGGED_IN_USER = new LoggedInUser();
    public static final Settings SETTINGS = new Settings();

    User[] users;
    private final String USER_KEY = "USERS", ME_KEY = "ME", SETTING_KEY = "BKGNDMUSIC";

    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if savedInstanceState is not null, the onRestoreInstanceState will keep the layout consistent of rotation
        // if savedInstanceState is null, load information of ME from internal storage
        if(savedInstanceState == null) {
            loadMe();
        }
    }

    public static void setSettings(Settings settings) {
        SETTINGS.setBackgroundMusicOn(settings.getBackgroundMusicOn());
    }

    public static void setLoggedInUser(LoggedInUser loggedInUser) {
        LOGGED_IN_USER.setSelfPortraitPath(loggedInUser.getSelfPortraitPath());
        LOGGED_IN_USER.setAddress(loggedInUser.getAddress());
        LOGGED_IN_USER.setCompany(loggedInUser.getCompany());
        LOGGED_IN_USER.setPhone(loggedInUser.getPhone());
        LOGGED_IN_USER.setWebsite(loggedInUser.getWebsite());
    }

    /**
     * save useful information, USERS array and ME
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String usersJson = GsonParser.getGsonParser().toJson(users);
        outState.putString(USER_KEY, usersJson);

        String meJson = GsonParser.getGsonParser().toJson(LOGGED_IN_USER);
        outState.putString(ME_KEY, meJson);

        String settingsJson = GsonParser.getGsonParser().toJson(SETTINGS);
        outState.putString(SETTING_KEY, settingsJson);
    }

    /**
     * restore useful information, USERS array and ME
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        users = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(USER_KEY), User[].class);

        LoggedInUser loggedInUser= GsonParser.getGsonParser().fromJson(savedInstanceState.getString(ME_KEY), LoggedInUser.class);
        NavigationPane.setLoggedInUser(loggedInUser);

        Settings settings = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(SETTING_KEY), Settings.class);
        NavigationPane.setSettings(settings);
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
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name))
                .setText(LOGGED_IN_USER.getName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email))
                .setText(LOGGED_IN_USER.getEmail());

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
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_fragment_container, new SettingsFragment(NavigationPane.SETTINGS), null)
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_channel)
                .setContentTitle("visit again")
                .setContentText("don't forget about LOGGED_IN_USER, tap to revisit")
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    /**
     * update the account
     * this method is called immediately after the end user successfully logged in
     * all other activities except the SignInActivity requires a menu
     * and the menu requires name and email address
     */
    public static void updateAccount(@NonNull GoogleSignInAccount account) {
        LOGGED_IN_USER.setName(account.getDisplayName());
        LOGGED_IN_USER.setEmail(account.getEmail());
    }

    /**
     * save information of ME in internal storage
     * specifically when end user finish editing his/her profile
     */
    protected void saveMe() {
        String text = GsonParser.getGsonParser().toJson(LOGGED_IN_USER);
        String fileName = LOGGED_IN_USER.getEmail() + ".txt";

        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(text.getBytes());
        } catch (IOException e) {
            Log.d("Save me", "file not found");
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * load information of ME from internal storage
     * specifically when there is no saved instance state
     */
    protected void loadMe() {
        FileInputStream fileInputStream = null;
        String fileName = LOGGED_IN_USER.getEmail() + ".txt";

        try {
            fileInputStream = openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            LoggedInUser loggedInUser = GsonParser.getGsonParser().fromJson(sb.toString(), LoggedInUser.class);
            NavigationPane.setLoggedInUser(loggedInUser);
        } catch (IOException e) {
            Log.d("Load me", "file not found");
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}