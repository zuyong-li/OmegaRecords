package com.learningandroid.omegarecords.storage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.learningandroid.omegarecords.storage.entity.User;
import com.learningandroid.omegarecords.utils.GsonProvider;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * a singleton local room database that serves as the only resource of the user list
 * if the database is empty, fetch data from the url first
 */
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    private static final String DB_NAME = "user_db";
    private static final String URL = "https://jsonplaceholder.typicode.com/users";
    private static UserDatabase userDatabase;

    public static synchronized UserDatabase getInstance(@NonNull Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DB_NAME)
                    .addCallback(fetchDataFromURL)
                    .build();
        }
        return userDatabase;
    }

    private static final int NUM_OF_THREADS = 4;
    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(NUM_OF_THREADS);

    private static final RoomDatabase.Callback fetchDataFromURL = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            dbExecutor.execute(() -> {
                UserDao userDao = userDatabase.userDao();

                // the local database is empty, fetch user list from url
                if (userDao.getAnyUser().length < 1) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(URL).build();

                    try {
                        // fetch user from url synchronously, since it is already running on an executor
                        Response response = okHttpClient.newCall(request).execute();
                        User[] users = GsonProvider.getInstance().fromJson(Objects.requireNonNull(response.body()).string(), User[].class);
                        userDao.insertUsers(users);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public abstract UserDao userDao();
}
