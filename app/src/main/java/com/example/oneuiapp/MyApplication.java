package com.example.oneuiapp;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.init(this);
        SettingsHelper.initializeFromSettings(this);
    }
}
