package com.example.oneuiapp;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

/**
 * BaseActivity ensures the Activity context is wrapped with app settings (locale/theme)
 * so layout direction and resources update correctly when language changes.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        // Wrap the context with SettingsHelper which applies locale and theme preferences
        super.attachBaseContext(SettingsHelper.wrapContext(newBase));
    }
}
