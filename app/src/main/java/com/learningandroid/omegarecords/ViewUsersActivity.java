package com.learningandroid.omegarecords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learningandroid.omegarecords.domain.*;
import com.learningandroid.omegarecords.utils.UserAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewUsersActivity extends AppCompatActivity {

    private static final String URL = "https://jsonplaceholder.typicode.com/users";
    private User[] users = null;
    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            fetchData();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() throws InterruptedException {
        while (users == null) {
            Thread.sleep(50);
        }

        fillData();
        findViewById(R.id.view_user_fetch_data).setVisibility(View.GONE);
        findViewById(R.id.view_user_fetch_progress).setVisibility(View.GONE);
        findViewById(R.id.view_user_cancel_button).setVisibility(View.GONE);
        findViewById(R.id.view_user_list).setVisibility(View.VISIBLE);
    }

    private void fillData() {
        RecyclerView recyclerView = findViewById(R.id.view_user_list);
        UserAdapter userAdapter = new UserAdapter(this, users);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchData() {
        Request request = new Request.Builder().url(URL).build();

        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                users = gson.fromJson(response.body().string(), User[].class);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Call Failure", "Failed to execute request");
                e.printStackTrace();
            }
        });
    }
}