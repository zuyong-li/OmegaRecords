package com.learningandroid.omegarecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.learningandroid.omegarecords.domain.User;
import com.learningandroid.omegarecords.utils.GsonParser;

import static com.learningandroid.omegarecords.App.CHANNEL_ID;

public class NavigationPane extends AppCompatActivity {

    static User[] users;
    ActionBarDrawerToggle actionBarDrawerToggle;
    static GoogleSignInAccount account;
    static User me = new User();
    static Bitmap selfPortrait;
    private final String USER_KEY = "USERS", ACCOUNT_KEY = "ACCOUNT", ME_KEY = "ME", SELF_PORTRAIT_KEY = "PORTRAIT";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save users, account and me
        String usersJson = GsonParser.getGsonParser().toJson(users);
        outState.putString(USER_KEY, usersJson);

        String accountJson = GsonParser.getGsonParser().toJson(account);
        outState.putString(ACCOUNT_KEY, accountJson);

        String meJson = GsonParser.getGsonParser().toJson(me);
        outState.putString(ME_KEY, meJson);

        String portraitJson = GsonParser.getGsonParser().toJson(selfPortrait);
        outState.putString(SELF_PORTRAIT_KEY, portraitJson);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        users = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(USER_KEY), User[].class);
        account = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(ACCOUNT_KEY), GoogleSignInAccount.class);
        me = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(ME_KEY), User.class);
        selfPortrait = GsonParser.getGsonParser().fromJson(savedInstanceState.getString(SELF_PORTRAIT_KEY), Bitmap.class);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, this.getIntent(), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_channel)
                .setContentTitle("visit again")
                .setContentText("don't forget about me, tap to revisit")
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    @SuppressLint("NonConstantResourceId")
    public void onCreateDrawer(DrawerLayout drawerLayout) {
        NavigationView navigationView = findViewById(R.id.navigation_pane);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up the name and email fields in the header
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name))
                .setText(account.getDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email))
                .setText(account.getEmail());

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
}