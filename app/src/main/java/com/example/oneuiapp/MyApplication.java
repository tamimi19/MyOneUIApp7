package com.example.oneuiapp;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsHelper.initializeFromSettings(this);
    }
}
