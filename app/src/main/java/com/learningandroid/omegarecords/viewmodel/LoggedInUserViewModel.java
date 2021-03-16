package com.learningandroid.omegarecords.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.learningandroid.omegarecords.viewmodel.repository.LoggedInUserRepository;
import com.learningandroid.omegarecords.db.entity.LoggedInUser;


/**
 * A view model that provides loading and saving details of the logged in user
 * logged in user is stored in app-specific internal storage
 */
public class LoggedInUserViewModel extends AndroidViewModel {

    private final LoggedInUserRepository repository = new LoggedInUserRepository();
    private final Application application;

    public LoggedInUserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LoggedInUser loadLoggedInUser(@NonNull LoggedInUser loggedInUser) {
        return repository.loadLoggedInUser(application, loggedInUser);
    }

    public void saveLoggedInUser(@NonNull LoggedInUser loggedInUser) {
        repository.saveLoggedInUser(application, loggedInUser);
    }

    public void saveSelfPortrait(@NonNull LoggedInUser loggedInUser) {
        repository.saveSelfPortraitToGallery(application, loggedInUser);
    }

    public Uri loadSelfPortrait(@NonNull LoggedInUser loggedInUser) {
        return repository.loadSelfPortraitFromGallery(loggedInUser);
    }

    public String getFilePathFromUri(@NonNull Uri uri) {
        return repository.getFilePathFromUri(application, uri);
    }
}
