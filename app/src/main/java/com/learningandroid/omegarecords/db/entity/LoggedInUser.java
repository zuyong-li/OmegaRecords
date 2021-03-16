package com.learningandroid.omegarecords.db.entity;

public class LoggedInUser extends User {
    String selfPortraitPath;

    public String getSelfPortraitPath() {
        return selfPortraitPath;
    }

    public void setSelfPortraitPath(String selfPortraitPath) {
        this.selfPortraitPath = selfPortraitPath;
    }
}
