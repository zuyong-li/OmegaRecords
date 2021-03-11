package com.learningandroid.omegarecords.component.activity;

import android.os.Bundle;

import com.learningandroid.omegarecords.R;

/**
 * the default activity to show up after google sign in succeeds
 * it displays general information of the app
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