package com.learningandroid.omegarecords.component.activity;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.db.UserDatabase;
import com.learningandroid.omegarecords.db.entity.*;
import com.learningandroid.omegarecords.utils.UserAdapter;
import com.learningandroid.omegarecords.viewmodel.ImageViewModel;
import com.learningandroid.omegarecords.viewmodel.UserViewModel;

import java.util.ArrayList;


/**
 * this activity displays a RecycerView of USERS array and the logged in user
 * if the Cardivew is clicked, redirect to the corresponding user details
 */
public class ViewUsersActivity extends NavigationPane {

    UserAdapter userAdapter;
    ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));
        setData();
    }

    /**
     * setup the userAdapter, initially contains only the loggedInUser
     * then data is fetched from the local database,
     * if the local database is empty, fetch data from the website
     * if data is fetched from website, save it to local database
     */
    private void setData() {
        loggedInUser = loadLoggedInUser();
        userList.add(loggedInUser);
        ImageViewModel imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        userAdapter = new UserAdapter(this, userList, imageViewModel, loggedInUserViewModel);

        RecyclerView recyclerView = findViewById(R.id.view_user_list);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUsers().observe(this, users -> {
            userList.addAll(users);
            userAdapter.notifyDataSetChanged();
            if (userViewModel.shouldSaveDataToDatabase()) {
                UserDatabase.getInstance(this).userRoomDao().insertUsers(users);
            }
        });
    }
}