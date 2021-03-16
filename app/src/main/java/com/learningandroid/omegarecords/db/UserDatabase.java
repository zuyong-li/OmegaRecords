package com.learningandroid.omegarecords.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.learningandroid.omegarecords.db.dao.UserRoomDao;
import com.learningandroid.omegarecords.db.entity.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DB_NAME = "user_db";
    private static UserDatabase userDatabase;

    public static synchronized UserDatabase getInstance(@NonNull Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return userDatabase;
    }

    public abstract UserRoomDao userRoomDao();
}
