package com.example.oneuiapp;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.init(this);
        SettingsHelper.initializeFromSettings(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(SettingsHelper.wrapContext(base));
    }
}
