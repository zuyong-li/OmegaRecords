package com.learningandroid.omegarecords.db.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.learningandroid.omegarecords.db.entity.User;
import com.learningandroid.omegarecords.utils.GsonProvider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserWebDao {

    private static final String URL = "https://jsonplaceholder.typicode.com/users";
    private static final String TAG = "WEB DAO";

    private static UserWebDao userWebDao;

    public static UserWebDao getInstance() {
        if (userWebDao == null) {
            userWebDao = new UserWebDao();
        }
        return userWebDao;
    }

    public LiveData<List<User>> getUsers() {
        MutableLiveData<List<User>> result = new MutableLiveData<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                User[] users = GsonProvider.getInstance().fromJson(Objects.requireNonNull(response.body()).string(), User[].class);
                if (response.isSuccessful()) {
                    result.postValue(Arrays.asList(users));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "failed to execute request of fetch users from url");
            }
        });
        return result;
    }
}
