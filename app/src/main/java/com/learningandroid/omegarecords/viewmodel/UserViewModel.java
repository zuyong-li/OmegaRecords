package com.learningandroid.omegarecords.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.learningandroid.omegarecords.OmegaRecordsApp;
import com.learningandroid.omegarecords.db.UserDatabase;
import com.learningandroid.omegarecords.db.entity.User;
import com.learningandroid.omegarecords.viewmodel.repository.UserRepository;

import java.util.List;

/**
 * a view model that provides a user list
 */
public class UserViewModel extends AndroidViewModel {

    private final LiveData<List<User>> users;
    private final UserRepository repository;
    private final UserDatabase userDatabase;

    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = UserRepository.getInstance();
        userDatabase = ((OmegaRecordsApp) application).getUserDatabase();
        users = repository.getUsers(userDatabase);
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public boolean shouldSaveDataToDatabase() {
        return repository.isEmpty(userDatabase);
    }
}
