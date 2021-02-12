package com.learningandroid.omegarecords.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonParser {
    private static Gson gson;
    public static Gson getGsonParser() {
        if(gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }
}
