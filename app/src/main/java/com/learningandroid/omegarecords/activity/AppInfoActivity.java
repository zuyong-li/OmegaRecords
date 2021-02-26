package com.learningandroid.omegarecords.activity;

import android.os.Bundle;

import com.learningandroid.omegarecords.R;

/**
 * purpose of this activity is to display general information of this app
 * after user login, this activity will show up
 */
public class AppInfoActivity extends NavigationPane {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));
    }
}