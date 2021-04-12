package com.learningandroid.omegarecords.component.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.learningandroid.omegarecords.R;
import com.learningandroid.omegarecords.storage.UserDatabase;
import com.learningandroid.omegarecords.storage.entity.*;
import com.learningandroid.omegarecords.utils.UserAdapter;
import com.learningandroid.omegarecords.viewmodel.ImageViewModel;
import com.learningandroid.omegarecords.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * this activity displays a RecycerView of USERS array and the logged in user
 * if the view holder is clicked, redirect to the corresponding user details
 * if the view holder is swiped to left or right, the corresponding user is removed from RoomDatabase
 */
public class ViewUsersActivity extends NavigationPane {

    UserAdapter userAdapter;
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));
        setData();
    }

    /**
     * setup the userAdapter, item touch helper for the recyclerview
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
        userViewModel.getUsers().observe(this, users -> userAdapter.setUsers(users));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                User user = userAdapter.userAt(position);
                userViewModel.deleteUser(user);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}