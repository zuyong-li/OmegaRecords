package com.learningandroid.omegarecords.domain;

public class LoggedInUser extends User{
    String selfPortraitPath;

    public String getSelfPortraitPath() {
        return selfPortraitPath;
    }

    public void setSelfPortraitPath(String selfPortraitPath) {
        this.selfPortraitPath = selfPortraitPath;
    }
}
