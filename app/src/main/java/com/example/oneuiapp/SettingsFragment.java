package com.example.oneuiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dev.oneuiproject.oneui.widget.Toast;
import dev.oneuiproject.oneui.layout.DrawerLayout;

public class SettingsFragment extends Fragment {

    private TextView languageValue;
    private TextView themeValue;
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
            // تعيين العنوان الكبير الرئيسي
            drawerLayout.setTitle(getString(R.string.title_settings));
            
            // تعيين العنوان الفرعي (subtitle) الذي يظهر أسفل العنوان الكبير
            // يمكنك تغيير النص إلى أي شيء تريده، مثل:
            // "Customize your app" أو "Manage preferences" أو أي نص آخر
            drawerLayout.setExpandedSubtitle(getString(R.string.settings_subtitle));
        }

        settingsHelper = new SettingsHelper(requireContext());

        languageValue = view.findViewById(R.id.language_value);
        themeValue = view.findViewById(R.id.theme_value);

        LinearLayout languageSetting = view.findViewById(R.id.language_setting);
        LinearLayout themeSetting = view.findViewById(R.id.theme_setting);
        LinearLayout notificationsSetting = view.findViewById(R.id.notifications_setting);

        updateLanguageValue();
        updateThemeValue();

        languageSetting.setOnClickListener(v -> showLanguageDialog());
        themeSetting.setOnClickListener(v -> showThemeDialog());
        notificationsSetting.setOnClickListener(v -> 
            Toast.makeText(requireContext(), getString(R.string.settings_notifications_placeholder), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // إعادة العنوان والعنوان الفرعي إلى القيم الأصلية عند مغادرة شاشة الإعدادات
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            // إعادة العنوان إلى "OneUI App"
            drawerLayout.setTitle(getString(R.string.app_name));
            
            // إزالة العنوان الفرعي بتعيينه إلى null أو نص فارغ
            // يمكنك تركه فارغاً أو وضع subtitle للشاشة الرئيسية إذا أردت
            drawerLayout.setExpandedSubtitle(getString(R.string.app_subtitle2));
        }
    }

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
            requireActivity().recreate();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

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

    private void updateLanguageValue() {
        int mode = settingsHelper.getLanguageMode();
        String[] options = {
            getString(R.string.settings_language_system),
            getString(R.string.settings_language_arabic),
            getString(R.string.settings_language_english)
        };
        languageValue.setText(options[mode]);
    }

    private void updateThemeValue() {
        int mode = settingsHelper.getThemeMode();
        String[] options = {
            getString(R.string.settings_theme_system),
            getString(R.string.settings_theme_light),
            getString(R.string.settings_theme_dark)
        };
        themeValue.setText(options[mode]);
    }
}
