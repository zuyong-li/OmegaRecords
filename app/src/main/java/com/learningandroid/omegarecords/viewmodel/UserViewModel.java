package com.learningandroid.omegarecords.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.learningandroid.omegarecords.storage.entity.User;
import com.learningandroid.omegarecords.storage.repository.UserRepository;

import java.util.List;

/**
 * a view model that provides a user list
 */
public class UserViewModel extends AndroidViewModel {

    private final LiveData<List<User>> users;
    private final UserRepository repository;


    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = new UserRepository(application);
        users = repository.getAllUsers();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void deleteUser(User user) {
        repository.deleteUser(user);
    }

    public void deleteAllUsers() {
        repository.deleteAllUsers();
    }

    public void insertUsers(User[] users) {
        repository.insertUsers(users);
    }
}
