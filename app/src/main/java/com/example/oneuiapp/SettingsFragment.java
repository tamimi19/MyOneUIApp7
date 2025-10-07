package com.example.oneuiapp;

import android.graphics.Color;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import dev.oneuiproject.oneui.widget.Toast;

/**
 * SettingsFragment معدل: كامل وموثوق أكثر.
 * - يحاول استدعاء PreferenceUtils.setPreferenceRoundedCornerColor عبر الانعكاس (بأي توقيع)
 * - إذا لم تنجح الطريقة، يمر عبر شجرة العرض ويستدعي setRoundedCornerColor على أي View اسمه يحتوي "Round"
 * - لا يغيّر ثيم أو موارد المكتبة (آمن للاستخدام مع AAR الرسمي)
 */
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

        // ===== حل OneUI: ضبط لون الحافة برمجياً =====
        int baseColor = resolveLibraryColorOrFallback("oui_round_and_bgcolor");
        int borderColor = ColorUtils.blendARGB(baseColor, Color.BLACK, 0.08f); // أغمق ~8%

        // 1) جرّب استدعاء PreferenceUtils.setPreferenceRoundedCornerColor(...) عبر الانعكاس
        boolean applied = tryApplyPreferenceUtils(borderColor);

        // 2) إذا لم ينجح، مرّر الشجرة وطبّق على كل View فيه اسم يحتوي "Round"
        if (!applied) {
            traverseAndApplyBorder(view, borderColor);
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
        languageValue.setText(options[Math.max(0, Math.min(mode, options.length - 1))]);
    }

    private void updateThemeValue() {
        int mode = settingsHelper.getThemeMode();
        String[] options = {
                getString(R.string.settings_theme_system),
                getString(R.string.settings_theme_light),
                getString(R.string.settings_theme_dark)
        };
        themeValue.setText(options[Math.max(0, Math.min(mode, options.length - 1))]);
    }

    /**
     * يحاول إيجاد لون داخل موارد التطبيق أو داخل حزمة المكتبة، وإلا يرجع لون الخلفية من الثيم.
     */
    private int resolveLibraryColorOrFallback(String colorName) {
        // 1) موارد التطبيق
        int id = getResources().getIdentifier(colorName, "color", requireContext().getPackageName());
        if (id != 0) {
            try {
                return ContextCompat.getColor(requireContext(), id);
            } catch (Exception ignored) { }
        }

        // 2) موارد المكتبة (حزمة AAR غالباً dev.oneuiproject.oneui)
        id = getResources().getIdentifier(colorName, "color", "dev.oneuiproject.oneui");
        if (id != 0) {
            try {
                return ContextCompat.getColor(requireContext(), id);
            } catch (Exception ignored) { }
        }

        // 3) ارجع لون الخلفية من الثيم
        try {
            int[] attrs = new int[]{android.R.attr.colorBackground};
            TypedArray ta = requireContext().getTheme().obtainStyledAttributes(attrs);
            int c = ta.getColor(0, Color.WHITE);
            ta.recycle();
            return c;
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    /**
     * يحاول استدعاء PreferenceUtils.setPreferenceRoundedCornerColor(...) بأي توقيع معقول:
     * - (androidx.preference.PreferenceFragmentCompat, int)
     * - (androidx.fragment.app.Fragment, int)
     * - (Context, int)  <-- نتحقق فقط، نستخدم الانعكاس بحذر
     *
     * يعيد true إذا نجحت الاستجابة.
     */
    private boolean tryApplyPreferenceUtils(int borderColor) {
        try {
            Class<?> prefUtils = Class.forName("dev.oneuiproject.oneui.utils.PreferenceUtils");
            // جرب التواقيع المحتملة
            try {
                // توقيع شائع: (androidx.preference.PreferenceFragmentCompat, int)
                Class<?> pfClass = Class.forName("androidx.preference.PreferenceFragmentCompat");
                java.lang.reflect.Method m = prefUtils.getMethod("setPreferenceRoundedCornerColor", pfClass, int.class);
                // ليس لدينا PreferenceFragmentCompat هنا؛ لا نملك مثيلاً. حاول إيجاد fragment في Activity أو تحويل هذا إلى pfClass إن أمكن - نتخطى.
            } catch (Exception ignored) { }

            // جرب (androidx.fragment.app.Fragment, int)
            try {
                java.lang.reflect.Method mFrag = prefUtils.getMethod("setPreferenceRoundedCornerColor", Fragment.class, int.class);
                mFrag.invoke(null, this, borderColor);
                return true;
            } catch (NoSuchMethodException nm) {
                // تابع لتواقيع أخرى
            }

            // جرب (android.content.Context, int)
            try {
                java.lang.reflect.Method mCtx = prefUtils.getMethod("setPreferenceRoundedCornerColor", android.content.Context.class, int.class);
                mCtx.invoke(null, requireContext(), borderColor);
                return true;
            } catch (NoSuchMethodException ignored) { }

            // جرب (androidx.preference.PreferenceFragmentCompat, int) بتمرير null آمن إذا لم نتيقن - تجاوز لأنه قد ينهار
            return false;
        } catch (ClassNotFoundException cnf) {
            // المكتبة لا تحتوي PrefUtils في وقت التشغيل (غير محتمل إذا أضفت AAR)، أو اسم الحزمة مختلف
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * يمر عبر شجرة العرض ويطبّق setRoundedCornerColor على أي View اسمه يحتوي "Round".
     */
    private void traverseAndApplyBorder(View root, int borderColor) {
        if (root == null) return;

        String className = root.getClass().getName();
        String simpleName = root.getClass().getSimpleName();

        boolean looksLikeRound = simpleName.startsWith("Round") || (className.contains("oneui") && className.contains("Round"));

        if (looksLikeRound) {
            // حاول setRoundedCornerColor(int mask, int color)
            try {
                java.lang.reflect.Method m = root.getClass().getMethod("setRoundedCornerColor", int.class, int.class);
                m.invoke(root, 0, borderColor); // 0 عادة يعني جميع الأركان
            } catch (NoSuchMethodException e1) {
                try {
                    java.lang.reflect.Method m2 = root.getClass().getMethod("setRoundedCornerColor", int.class);
                    m2.invoke(root, borderColor);
                } catch (NoSuchMethodException e2) {
                    // بعض النسخ قد تستخدم اسمًا مختلفًا، نجرب setRoundCornerColor
                    try {
                        java.lang.reflect.Method m3 = root.getClass().getMethod("setRoundCornerColor", int.class);
                        m3.invoke(root, borderColor);
                    } catch (Exception ignored) { }
                } catch (Exception ignored) { }
            } catch (Exception ignored) { }
        }

        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                traverseAndApplyBorder(vg.getChildAt(i), borderColor);
            }
        }
    }
}
