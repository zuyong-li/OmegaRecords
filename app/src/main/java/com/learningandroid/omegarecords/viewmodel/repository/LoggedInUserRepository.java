package com.learningandroid.omegarecords.viewmodel.repository;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.learningandroid.omegarecords.db.entity.LoggedInUser;
import com.learningandroid.omegarecords.utils.GsonProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggedInUserRepository {

    private static final String TAG = "DAO";

    /**
     * load the logged in user details from the app-specific internal storage
     */
    public LoggedInUser loadLoggedInUser(@NonNull Context context, @NonNull LoggedInUser loggedInUser) {
        LoggedInUser result;
        try {
            FileInputStream fileInputStream = context.openFileInput(loggedInUser.getName());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            result = GsonProvider.getInstance().fromJson(
                    stringBuilder.toString(), LoggedInUser.class);
        } catch (IOException e) {
            result = loggedInUser;
            Log.d(TAG, "file does not exist when loading the logged in user details");
        }
        return result;
    }

    /**
     * save the logged in user details into the app-specific internal storage
     */
    public void saveLoggedInUser(@NonNull Context context, @NonNull LoggedInUser loggedInUser) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(loggedInUser.getName(), Context.MODE_PRIVATE);
            String fileContents = GsonProvider.getInstance().toJson(loggedInUser);
            fileOutputStream.write(fileContents.getBytes());
        } catch (IOException e) {
            Log.d(TAG, "file does not exist when saving the logged in user details");
        }
    }

    /**
     * save a selfPortrait image into gallery
     * this method will be called after a photo is successfully taken
     * and its filePath has been set to the selfPortraitPath of the logged in user
     */
    public void saveSelfPortraitToGallery(@NonNull Context context, @NonNull LoggedInUser loggedInUser) {
        String path = loggedInUser.getSelfPortraitPath();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            context.sendBroadcast(mediaScanIntent);
        }
    }

    /**
     * get the selfPortrait uri of the logged in user
     */
    public Uri loadSelfPortraitFromGallery(@NonNull LoggedInUser loggedInUser) {
        String path = loggedInUser.getSelfPortraitPath();
        if (path == null) {
            return null;
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    /**
     * get the real path of an image picked from gallery
     * this method will be called after a profile image is successfully picked from gallery
     */
    public String getFilePathFromUri(@NonNull Context context, @NonNull Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
