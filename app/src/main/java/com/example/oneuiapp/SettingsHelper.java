package com.example.oneuiapp;

import android.app.UiModeManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

/**
 * SettingsHelper يدير جميع إعدادات التطبيق بطريقة مركزية
 * يتضمن إعدادات اللغة، الثيم، والإشعارات
 */
public class SettingsHelper {
    
    // مفاتيح SharedPreferences
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_LANGUAGE = "language_mode";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    
    // قيم اللغة
    public static final int LANGUAGE_SYSTEM = 0;
    public static final int LANGUAGE_ARABIC = 1;
    public static final int LANGUAGE_ENGLISH = 2;
    
    // قيم الثيم
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    private Context mContext;
    private SharedPreferences mPrefs;

    public SettingsHelper(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ============ التهيئة الأولية ============
    
    /**
     * تهيئة إعدادات التطبيق عند بدء التشغيل
     * يتم استدعاء هذه الدالة من MyApplication.onCreate()
     * لتطبيق الثيم المحفوظ قبل إنشاء أي Activity
     * @param context سياق التطبيق
     */
    public static void initializeFromSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, THEME_SYSTEM);
        
        // تطبيق الثيم المحفوظ على مستوى التطبيق بأكمله
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    // ============ إدارة اللغة ============
    
    /**
     * الحصول على وضع اللغة المحفوظ
     * @return وضع اللغة (LANGUAGE_SYSTEM, LANGUAGE_ARABIC, أو LANGUAGE_ENGLISH)
     */
    public int getLanguageMode() {
        return mPrefs.getInt(KEY_LANGUAGE, LANGUAGE_SYSTEM);
    }

    /**
     * حفظ وضع اللغة الجديد
     * @param mode وضع اللغة المطلوب
     */
    public void setLanguageMode(int mode) {
        mPrefs.edit().putInt(KEY_LANGUAGE, mode).apply();
    }

    /**
     * الحصول على الـ Locale المناسب بناءً على الإعدادات المحفوظة
     * @param context السياق المطلوب
     * @return الـ Locale المناسب
     */
    public static Locale getLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int mode = prefs.getInt(KEY_LANGUAGE, LANGUAGE_SYSTEM);
        
        switch (mode) {
            case LANGUAGE_ARABIC:
                return new Locale("ar");
            case LANGUAGE_ENGLISH:
                return new Locale("en");
            default:
                // استخدام لغة النظام
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return context.getResources().getConfiguration().getLocales().get(0);
                } else {
                    return context.getResources().getConfiguration().locale;
                }
        }
    }

    // ============ إدارة الثيم ============
    
    /**
     * الحصول على وضع الثيم المحفوظ
     * @return وضع الثيم (THEME_SYSTEM, THEME_LIGHT, أو THEME_DARK)
     */
    public int getThemeMode() {
        return mPrefs.getInt(KEY_THEME, THEME_SYSTEM);
    }

    /**
     * حفظ وضع الثيم الجديد
     * @param mode وضع الثيم المطلوب
     */
    public void setThemeMode(int mode) {
        mPrefs.edit().putInt(KEY_THEME, mode).apply();
    }

    /**
     * تطبيق الثيم المحفوظ على التطبيق
     * يتم استدعاء هذه الدالة عند تغيير الثيم من الإعدادات
     */
    public void applyTheme() {
        int mode = getThemeMode();
        switch (mode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    // ============ إدارة الإشعارات ============
    
    /**
     * التحقق من تفعيل الإشعارات
     * @return true إذا كانت الإشعارات مفعّلة، false إذا كانت معطّلة
     */
    public boolean areNotificationsEnabled() {
        // القيمة الافتراضية هي true (مفعّلة)
        return mPrefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    /**
     * تفعيل أو تعطيل الإشعارات
     * @param enabled true لتفعيل الإشعارات، false لتعطيلها
     */
    public void setNotificationsEnabled(boolean enabled) {
        mPrefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
        
        // هنا يمكنك إضافة منطق إضافي لتطبيق التغييرات فوراً
        // مثل إلغاء جميع الإشعارات المجدولة إذا تم التعطيل
        // أو إعادة جدولتها إذا تم التفعيل
        
        if (enabled) {
            // تم تفعيل الإشعارات - يمكنك هنا إضافة كود لإعادة تفعيل الإشعارات
            // مثال: scheduleNotifications();
        } else {
            // تم تعطيل الإشعارات - يمكنك هنا إضافة كود لإلغاء جميع الإشعارات
            // مثال: cancelAllNotifications();
        }
    }

    // ============ Context Wrapper للغة والثيم ============
    
    /**
     * إنشاء ContextWrapper يطبق إعدادات اللغة والثيم
     * يُستخدم في attachBaseContext في BaseActivity
     * @param context السياق الأصلي
     * @return ContextWrapper مع الإعدادات المطبقة
     */
    public static ContextWrapper wrapContext(Context context) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale locale = getLocale(context);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLocales(new LocaleList(locale));
        } else {
            config.locale = locale;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(locale);
        }
        
        // تطبيق الثيم
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, THEME_SYSTEM);
        
        int uiMode;
        if (themeMode == THEME_LIGHT) {
            uiMode = Configuration.UI_MODE_NIGHT_NO;
        } else if (themeMode == THEME_DARK) {
            uiMode = Configuration.UI_MODE_NIGHT_YES;
        } else {
            // استخدام إعدادات النظام
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            uiMode = uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES 
                    ? Configuration.UI_MODE_NIGHT_YES 
                    : Configuration.UI_MODE_NIGHT_NO;
        }
        
        config.uiMode = (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK) | uiMode;
        
        Context newContext = context.createConfigurationContext(config);
        return new ContextWrapper(newContext);
    }
}
