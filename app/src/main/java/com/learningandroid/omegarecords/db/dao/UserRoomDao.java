package com.learningandroid.omegarecords.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.learningandroid.omegarecords.db.entity.User;

import java.util.List;

/**
 * Data Access Object: an interface provides data access to local database
 */
@Dao
public interface UserRoomDao {

    @Query("SELECT * FROM user")
    LiveData<List<User>> getUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);

    @Query("SELECT COUNT(*) FROM user")
    int getUserCounts();

}

