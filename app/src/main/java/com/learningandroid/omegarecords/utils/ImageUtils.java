package com.learningandroid.omegarecords.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.learningandroid.omegarecords.db.entity.LoggedInUser;
import com.learningandroid.omegarecords.db.entity.User;
import com.learningandroid.omegarecords.viewmodel.ImageViewModel;
import com.learningandroid.omegarecords.viewmodel.LoggedInUserViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageUtils {

    private static final String USER_URL = "https://robohash.org/";
    private static final String COM_URL = "https://source.unsplash.com/random/200x200?sig=";
    private static final String TAG = "LOAD IMAGE";

    /**
     * set the user's profile image
     * if check if the user is the logged in user, if yes, try to display the selfPortrait
     * otherwise, search an image in the cache or load an image using Picasso
     */

    public static void setUserImage(ImageView imageView, User user, ImageViewModel imageViewModel,
                                    LoggedInUserViewModel loggedInUserViewModel) {
        Uri selfPortrait = null;
        if (user instanceof LoggedInUser) {
            selfPortrait = loggedInUserViewModel.loadSelfPortrait((LoggedInUser) user);
        }
        if (selfPortrait != null) {
            imageView.setImageURI(selfPortrait);
        } else {
            setImageHelper(imageView, USER_URL + user.getName(), imageViewModel);
        }
    }

    /**
     * set the company image
     */
    public static void setCompanyImage(ImageView imageView, User user, ImageViewModel imageViewModel) {
        String key = COM_URL + user.getCompany().getName();
        setImageHelper(imageView, key, imageViewModel);
    }

    /**
     * set an image referenced by KEY into the imageView
     * first, check if the image is present in the cache provided by imageViewModel
     * if the image is not in the cache, load a new image using Picasso
     */
    private static void setImageHelper(ImageView imageView, String key, ImageViewModel imageViewModel) {
        Bitmap bitmap = imageViewModel.getBitmapFromMemCache(key);
        if (bitmap != null) {
            Log.i(TAG, "load image from memory cache");
            imageView.setImageBitmap(bitmap);
        } else {
            Log.i(TAG, "load image from Picasso");
            Picasso.get().load(key).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(bitmap);
                    imageViewModel.addBitmapToMemCache(key, bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.d(TAG, "Faild to load image from Picasso");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }
}
