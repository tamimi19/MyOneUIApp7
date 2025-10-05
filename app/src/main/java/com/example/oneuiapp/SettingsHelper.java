package com.example.oneuiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class SettingsHelper {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_LANGUAGE_MODE = "language_mode";
    private static final String KEY_THEME_MODE = "theme_mode";

    public static final int LANGUAGE_SYSTEM = 0;
    public static final int LANGUAGE_ARABIC = 1;
    public static final int LANGUAGE_ENGLISH = 2;

    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    private SharedPreferences prefs;
    private Context context;

    public SettingsHelper(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getLanguageMode() {
        return prefs.getInt(KEY_LANGUAGE_MODE, LANGUAGE_SYSTEM);
    }

    public void setLanguageMode(int mode) {
        prefs.edit().putInt(KEY_LANGUAGE_MODE, mode).apply();
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM);
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public void applyTheme() {
        int mode = getThemeMode();

        switch (mode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public static Context wrapContext(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int languageMode = prefs.getInt(KEY_LANGUAGE_MODE, LANGUAGE_SYSTEM);

        Locale locale;
        switch (languageMode) {
            case LANGUAGE_ARABIC:
                locale = new Locale("ar");
                break;
            case LANGUAGE_ENGLISH:
                locale = new Locale("en");
                break;
            case LANGUAGE_SYSTEM:
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = Resources.getSystem().getConfiguration().getLocales().get(0);
                } else {
                    locale = Resources.getSystem().getConfiguration().locale;
                }
                break;
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }

    public static void initializeFromSettings(Context context) {
        SettingsHelper helper = new SettingsHelper(context);
        helper.applyTheme();
    }
}
