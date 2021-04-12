package com.learningandroid.omegarecords.storage.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.learningandroid.omegarecords.storage.UserDao;
import com.learningandroid.omegarecords.storage.UserDatabase;
import com.learningandroid.omegarecords.storage.entity.User;

import java.util.List;

public class UserRepository {

    private final UserDao userDao;
    private final LiveData<List<User>> users;

    public UserRepository(Application application) {
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.userDao();
        users = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return users;
    }

    public void deleteUser(User user) {
        UserDatabase.dbExecutor.execute(() -> userDao.deleteUser(user));
    }

    public void deleteAllUsers() {
        UserDatabase.dbExecutor.execute(userDao::deleteAll);
    }

    public void insertUsers(User[] users) {
        UserDatabase.dbExecutor.execute(() -> userDao.insertUsers(users));
    }
}
