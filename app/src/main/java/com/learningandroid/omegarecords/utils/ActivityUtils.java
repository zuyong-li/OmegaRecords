package com.learningandroid.omegarecords.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;


/**
 * A util class provides a singleton Gson,
 * load and save details and settings of loggedInUser using internal storage
 */
public class ActivityUtils<T> {
    private static Gson gson;

    /**
     *  return a Gson to convert objects and Json strings
     */
    public static Gson getGsonParser() {
        if(gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }

    /**
     * load data from the file specified by fileName and return an object of type T
     * if file does not exist, return the defaultValue
     */
    public T loadData(@NonNull Context context, @NonNull String fileName, @NonNull T defaultValue) {
        FileInputStream fileInputStream = null;
        T result;

        try {
            fileInputStream = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = ActivityUtils.getGsonParser().fromJson(sb.toString(), (Type) defaultValue.getClass());
        } catch (IOException e) {
            result = defaultValue;
            Log.d("Load settings", "file not found");
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * save string text to file specified by the filename
     */
    public static void saveData(@NonNull Context context, @NonNull String text, @NonNull String fileName) {
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(text.getBytes());
        } catch (IOException e) {
            Log.d("Save data", "file not found");
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
