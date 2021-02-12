package com.learningandroid.omegarecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.learningandroid.omegarecords.domain.User;

public class NavigationPane extends AppCompatActivity {

    static User[] users;
    ActionBarDrawerToggle actionBarDrawerToggle;
    static GoogleSignInAccount account;
    static User me = new User();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                case R.id.setting:
                    Intent settingIntent = new Intent();
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