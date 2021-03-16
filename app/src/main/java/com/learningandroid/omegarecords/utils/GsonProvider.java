package com.learningandroid.omegarecords.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A util class that provides a singleton Gson,
 */
public class GsonProvider {

    private static Gson gson;

    public static Gson getInstance() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }
}
