package com.example.oneuiapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
        
        // استعادة الحالة المحفوظة إذا كانت موجودة (بعد تغيير اللغة أو الثيم أو تدوير الشاشة)
        if (savedInstanceState != null) {
            mCurrentFragmentIndex = savedInstanceState.getInt(KEY_CURRENT_FRAGMENT, 0);
            
            // محاولة استعادة الـ Fragment من FragmentManager
            // عندما يتم recreate، FragmentManager يستعيد الـ Fragments تلقائياً
            FragmentManager fm = getSupportFragmentManager();
            Fragment existingFragment = fm.findFragmentById(R.id.main_content);
            
            // إذا لم يكن هناك Fragment معروض حالياً، عرض الـ Fragment المحفوظ
            if (existingFragment == null) {
                showFragment(mCurrentFragmentIndex, false);
            }
        } else {
            // أول مرة يتم فيها إنشاء Activity - عرض الشاشة الرئيسية
            showFragment(mCurrentFragmentIndex, false);
        }
        
        // إعداد الدرج بعد استعادة الحالة لضمان تحديد العنصر الصحيح
        setupDrawer();
    }

    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListView = findViewById(R.id.drawer_list_view);
    }

    private void initFragmentsList() {
        // إنشاء قائمة الـ Fragments - استخدم نفس Fragments في كل مرة
        mFragments.clear();
        mFragments.add(new HomeFragment());
        mFragments.add(new SettingsFragment());
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
                        showFragment(position, true);
                        return true; // تم التغيير بنجاح
                    }
                    return false; // لم يتم التغيير (نفس العنصر)
                });
        
        mDrawerListView.setAdapter(mDrawerAdapter);
        
        // تحديد العنصر الحالي في القائمة
        // هذا مهم جداً خاصة بعد recreate() لضمان ظهور التحديد الصحيح
        mDrawerAdapter.setSelectedItem(mCurrentFragmentIndex);
    }

    /**
     * عرض Fragment في الموضع المحدد
     * @param position موضع الـ Fragment في القائمة
     * @param addToBackStack هل نضيف هذه العملية إلى back stack
     */
    private void showFragment(int position, boolean addToBackStack) {
        if (position < 0 || position >= mFragments.size()) {
            return;
        }
        
        Fragment fragment = mFragments.get(position);
        FragmentManager fm = getSupportFragmentManager();
        
        // استخدام tag فريد لكل Fragment لتجنب التكرار
        String tag = "fragment_" + position;
        
        // التحقق من وجود Fragment بنفس الـ tag
        Fragment existingFragment = fm.findFragmentByTag(tag);
        
        if (existingFragment != null && existingFragment.isAdded()) {
            // الـ Fragment موجود بالفعل، فقط أظهره
            fm.beginTransaction()
                    .replace(R.id.main_content, existingFragment, tag)
                    .commit();
        } else {
            // الـ Fragment غير موجود، أنشئه وأضفه
            fm.beginTransaction()
                    .replace(R.id.main_content, fragment, tag)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // حفظ موضع الـ Fragment الحالي
        // هذا مهم جداً عند تغيير اللغة أو الثيم لأن Activity يتم إعادة إنشائها
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
