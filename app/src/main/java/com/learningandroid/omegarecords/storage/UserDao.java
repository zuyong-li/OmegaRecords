package com.learningandroid.omegarecords.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.learningandroid.omegarecords.storage.entity.User;

import java.util.List;

/**
 * Data Access Object: an interface provides data access to local database
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(User[] users);

    @Query("SELECT * FROM user LIMIT 1")
    User[] getAnyUser();

    @Query("DELETE FROM user")
    void deleteAll();

    @Delete
    void deleteUser(User user);
}

