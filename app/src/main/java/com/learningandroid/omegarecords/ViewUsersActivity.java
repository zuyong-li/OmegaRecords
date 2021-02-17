package com.learningandroid.omegarecords;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learningandroid.omegarecords.domain.*;
import com.learningandroid.omegarecords.utils.UserAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * this activity displays a RecycerView of USERS array and ME
 * if the Cardivew is clicked, redirect to the corresponding user details
 */
public class ViewUsersActivity extends NavigationPane {

    private static final String URL = "https://jsonplaceholder.typicode.com/users";
    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // create and setup the menu
        onCreateDrawer(findViewById(R.id.drawer_layout));
        fetchData();
    }

    /**
     * after the USERS array is fetched from internet
     * set USERS array to recyclerview
     */
    private void setData() {
        RecyclerView recyclerView = findViewById(R.id.view_user_list);
        UserAdapter userAdapter = new UserAdapter(this, users, me);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * fetch data from internet
     * if the data has already fetched, just display it.
     */
    private void fetchData() {
        if (users != null) {
            setData();
            return;
        }

        // using a progress dialog to inform the end user that data is still loading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data ...");
        progressDialog.show();

        Request request = new Request.Builder().url(URL).build();
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                users = gson.fromJson(Objects.requireNonNull(response.body()).string(), User[].class);

                // update the ui thread by cancelling the progress dialog and display the RecyclerView
                ViewUsersActivity.this.runOnUiThread(() -> {
                    progressDialog.cancel();
                    if(response.isSuccessful()) {
                        setData();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Call Failure", "Failed to execute request");
                e.printStackTrace();
            }
        });
    }
}