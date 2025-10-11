package com.example.oneuiapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import dev.oneuiproject.oneui.layout.DrawerLayout;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerListView;
    private DrawerListAdapter mDrawerAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private int mCurrentFragmentIndex = 0;
    
    // مفتاح لحفظ واستعادة الـ Fragment الحالي
    private static final String KEY_CURRENT_FRAGMENT = "current_fragment_index";
    
    // Tags للـ Fragments لاستخدامها في FragmentManager
    private static final String TAG_HOME = "homefragment";
    private static final String TAG_SETTINGS = "settingsfragment";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SettingsHelper.wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFragmentsList();
        
        // استعادة الحالة المحفوظة إذا كانت موجودة
        if (savedInstanceState != null) {
            mCurrentFragmentIndex = savedInstanceState.getInt(KEY_CURRENT_FRAGMENT, 0);
            
            // استعادة الـ Fragments من FragmentManager إذا كانت موجودة
            // هذا يحدث عند إعادة إنشاء Activity بعد تغيير اللغة أو تدوير الشاشة
            FragmentManager fm = getSupportFragmentManager();
            Fragment homeFragment = fm.findFragmentByTag(TAG_HOME);
            Fragment settingsFragment = fm.findFragmentByTag(TAG_SETTINGS);
            
            // إذا تم العثور على الـ Fragments المحفوظة، استخدمها بدلاً من إنشاء جديدة
            if (homeFragment != null && settingsFragment != null) {
                mFragments.clear();
                mFragments.add(homeFragment);
                mFragments.add(settingsFragment);
            }
            
            // إظهار الـ Fragment الصحيح وإخفاء الباقي
            showFragmentFast(mCurrentFragmentIndex);
        } else {
            // أول مرة يتم فيها إنشاء Activity - إضافة جميع الـ Fragments مرة واحدة
            // نستخدم add() بدلاً من replace() ونخفي جميع الـ Fragments ما عدا الأول
            addAllFragments();
        }
        
        // إعداد الدرج بعد استعادة الحالة
        setupDrawer();
    }

    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListView = findViewById(R.id.drawer_list_view);
    }

    private void initFragmentsList() {
        // إنشاء قائمة الـ Fragments إذا كانت فارغة
        // هذا يحدث فقط في المرة الأولى التي يتم فيها إنشاء Activity
        if (mFragments.isEmpty()) {
            mFragments.add(new HomeFragment());
            mFragments.add(new SettingsFragment());
        }
    }

    /**
     * إضافة جميع الـ Fragments مرة واحدة عند أول إنشاء للـ Activity
     * يتم إخفاء جميع الـ Fragments ما عدا الأول
     * هذا النمط يُسمى "Fragment Caching" ويجعل الانتقال بين الشاشات فورياً وسلساً
     */
    private void addAllFragments() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        
        // إضافة Home Fragment وإظهاره
        transaction.add(R.id.main_content, mFragments.get(0), TAG_HOME);
        
        // إضافة Settings Fragment وإخفاؤه
        transaction.add(R.id.main_content, mFragments.get(1), TAG_SETTINGS);
        transaction.hide(mFragments.get(1));
        
        // تنفيذ العملية دفعة واحدة لتحسين الأداء
        transaction.commit();
    }

    private void setupDrawer() {
        mDrawerListView.setLayoutManager(new LinearLayoutManager(this));
        mDrawerAdapter = new DrawerListAdapter(
                this,
                mFragments,
                position -> {
                    // دائماً أغلق الدرج عند الضغط على أي عنصر
                    mDrawerLayout.setDrawerOpen(false, true);
                    
                    // إذا كان العنصر المضغوط مختلفاً عن العنصر الحالي، غير الـ Fragment
                    if (position != mCurrentFragmentIndex) {
                        mCurrentFragmentIndex = position;
                        // استخدام showFragmentFast بدلاً من showFragment العادية
                        // للحصول على انتقال فوري وسلس بدون أي تأخير
                        showFragmentFast(position);
                        return true;
                    }
                    return false;
                });
        
        mDrawerListView.setAdapter(mDrawerAdapter);
        mDrawerAdapter.setSelectedItem(mCurrentFragmentIndex);
    }

    /**
     * عرض Fragment باستخدام show() و hide() بدلاً من replace()
     * هذا النمط أسرع بكثير لأنه لا يتطلب إعادة إنشاء الـ Views
     * بل فقط إخفاء وإظهار Views موجودة بالفعل في الذاكرة
     * 
     * @param position موضع الـ Fragment المطلوب عرضه
     */
    private void showFragmentFast(int position) {
        if (position < 0 || position >= mFragments.size()) {
            return;
        }
        
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        
        // إخفاء جميع الـ Fragments
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (fragment.isAdded()) {
                transaction.hide(fragment);
            }
        }
        
        // إظهار الـ Fragment المطلوب فقط
        Fragment targetFragment = mFragments.get(position);
        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        }
        
        // استخدام commitNow() بدلاً من commit() للتنفيذ الفوري
        // هذا يضمن عدم وجود أي تأخير حتى لو كان جزءاً من الثانية
        transaction.commitNow();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // حفظ موضع الـ Fragment الحالي
        outState.putInt(KEY_CURRENT_FRAGMENT, mCurrentFragmentIndex);
    }

    @Override
    public void onBackPressed() {
        // إصلاح memory leak في Android O
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTaskRoot()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }
    
    /**
     * دالة مساعدة لتحديث العنصر المحدد في الدرج
     * يمكن استدعاؤها من الـ Fragments إذا احتاجوا تحديث التحديد يدوياً
     */
    public void updateDrawerSelection(int position) {
        if (mDrawerAdapter != null && position >= 0 && position < mFragments.size()) {
            mCurrentFragmentIndex = position;
            mDrawerAdapter.setSelectedItem(position);
        }
    }
                        }
