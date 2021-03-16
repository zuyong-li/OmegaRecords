package com.learningandroid.omegarecords.viewmodel;


import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

/**
 * a view model that provides image access
 */
public class ImageViewModel extends ViewModel {

    private static final String TAG = "IMAGE CACHE";
    private static final LruCache<String, Bitmap> memoryCache;

    static { // initialize a memory cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        memoryCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * look up the corresponding image with the given KEY
     * if the image is not present, return null
     */
    public Bitmap getBitmapFromMemCache(@NonNull String key) {
        return memoryCache.get(key);
    }

    public void addBitmapToMemCache(@NonNull String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }
}
