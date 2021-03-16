package com.learningandroid.omegarecords.viewmodel.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.learningandroid.omegarecords.db.dao.UserWebDao;
import com.learningandroid.omegarecords.db.UserDatabase;
import com.learningandroid.omegarecords.db.entity.User;

import java.util.List;

public class UserRepository {

    private static UserRepository userRepository;
    private static final String TAG = "FETCH DATA";

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    /**
     * return observable data holder for a list of users
     * first, try to fetch the user list from local database
     * if the local database is empty, fetch the user list from url
     */
    public LiveData<List<User>> getUsers(UserDatabase userDatabase) {
        if (isEmpty(userDatabase)) {
            UserWebDao userWebDao = UserWebDao.getInstance();
            Log.i(TAG, "user data fetched from website");
            return userWebDao.getUsers();
        } else {
            Log.i(TAG, "user data fetched from database");
            return userDatabase.userRoomDao().getUsers();
        }
    }

    /**
     * check if the database is empty by counting the rows in the database
     */
    public boolean isEmpty(UserDatabase userDatabase) {
        return userDatabase.userRoomDao().getUserCounts() == 0;
    }
}
