package com.example.oneuiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import dev.oneuiproject.oneui.layout.DrawerLayout;
import dev.oneuiproject.oneui.widget.Toast;

public class SettingsFragment extends Fragment {

    private TextView languageValue;
    private TextView themeValue;
    private SwitchCompat notificationsSwitch;
    private SettingsHelper settingsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // تغيير عنوان DrawerLayout إلى "Settings" وإضافة subtitle
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.setTitle(getString(R.string.title_settings));
            drawerLayout.setExpandedSubtitle(getString(R.string.settings_subtitle));
        }

        // تهيئة SettingsHelper
        settingsHelper = new SettingsHelper(requireContext());

        // ربط العناصر من الـ layout
        languageValue = view.findViewById(R.id.language_value);
        themeValue = view.findViewById(R.id.theme_value);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);

        LinearLayout languageSetting = view.findViewById(R.id.language_setting);
        LinearLayout themeSetting = view.findViewById(R.id.theme_setting);
        LinearLayout notificationsSetting = view.findViewById(R.id.notifications_setting);

        // تحديث القيم المعروضة من الإعدادات المحفوظة
        updateLanguageValue();
        updateThemeValue();
        updateNotificationsSwitch();

        // إعداد معالجات الضغط والتغيير
        languageSetting.setOnClickListener(v -> showLanguageDialog());
        themeSetting.setOnClickListener(v -> showThemeDialog());
        
        // معالج التغيير للـ Switch
        // نستخدم setOnCheckedChangeListener بدلاً من setOnClickListener
        // لأنه يتم استدعاؤه فقط عندما تتغير حالة الـ Switch فعلياً
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // حفظ الحالة الجديدة
            settingsHelper.setNotificationsEnabled(isChecked);
            
            // إظهار رسالة توضيحية للمستخدم
            String message = isChecked 
                    ? getString(R.string.notifications_enabled) 
                    : getString(R.string.notifications_disabled);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
        
        // يمكن أيضاً جعل الضغط على العنصر بأكمله يغير حالة الـ Switch
        notificationsSetting.setOnClickListener(v -> {
            // عكس حالة الـ Switch الحالية
            notificationsSwitch.setChecked(!notificationsSwitch.isChecked());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // إعادة العنوان والعنوان الفرعي إلى القيم الأصلية عند مغادرة شاشة الإعدادات
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            drawerLayout.setTitle(getString(R.string.app_name));
            drawerLayout.setExpandedSubtitle(getString(R.string.app_subtitle));
        }
    }

    /**
     * عرض نافذة حوارية لاختيار اللغة
     */
    private void showLanguageDialog() {
        String[] options = {
            getString(R.string.settings_language_system),
            getString(R.string.settings_language_arabic),
            getString(R.string.settings_language_english)
        };

        int currentSelection = settingsHelper.getLanguageMode();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.settings_language);
        builder.setSingleChoiceItems(options, currentSelection, (dialog, which) -> {
            settingsHelper.setLanguageMode(which);
            updateLanguageValue();
            dialog.dismiss();
            // إعادة إنشاء Activity لتطبيق اللغة الجديدة
            requireActivity().recreate();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    /**
     * عرض نافذة حوارية لاختيار الثيم
     */
    private void showThemeDialog() {
        String[] options = {
            getString(R.string.settings_theme_system),
            getString(R.string.settings_theme_light),
            getString(R.string.settings_theme_dark)
        };

        int currentSelection = settingsHelper.getThemeMode();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.settings_theme);
        builder.setSingleChoiceItems(options, currentSelection, (dialog, which) -> {
            settingsHelper.setThemeMode(which);
            updateThemeValue();
            settingsHelper.applyTheme();
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    /**
     * تحديث النص المعروض لقيمة اللغة المحفوظة
     */
    private void updateLanguageValue() {
        int mode = settingsHelper.getLanguageMode();
        String[] options = {
            getString(R.string.settings_language_system),
            getString(R.string.settings_language_arabic),
            getString(R.string.settings_language_english)
        };
        languageValue.setText(options[mode]);
    }

    /**
     * تحديث النص المعروض لقيمة الثيم المحفوظ
     */
    private void updateThemeValue() {
        int mode = settingsHelper.getThemeMode();
        String[] options = {
            getString(R.string.settings_theme_system),
            getString(R.string.settings_theme_light),
            getString(R.string.settings_theme_dark)
        };
        themeValue.setText(options[mode]);
    }

    /**
     * تحديث حالة زر الإشعارات من الإعدادات المحفوظة
     */
    private void updateNotificationsSwitch() {
        // الحصول على الحالة المحفوظة وتطبيقها على الـ Switch
        boolean enabled = settingsHelper.areNotificationsEnabled();
        notificationsSwitch.setChecked(enabled);
    }
}
