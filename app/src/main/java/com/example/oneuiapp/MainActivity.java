package com.example.oneuiapp;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneui.layout.DrawerLayout;
import dev.oneuiproject.oneui.utils.internal.ToolbarLayoutUtils;

public class MainActivity extends AppCompatActivity {

    private boolean mEnableBackToHeader;
    private DrawerLayout mDrawerLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private Toolbar mToolbar;
    private View mSwipeUpContainer;
    private ViewGroup mBottomContainer;
    private RecyclerView mDrawerListView;
    private DrawerListAdapter mDrawerAdapter;

    private List<Fragment> mFragments = new ArrayList<>();
    private int mCurrentFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupDrawer();

        // طبق padding للقائمة بعد تهيئة الـ toolbar و الـ drawer (مطابق للـ sample-app)
        applyDrawerTopPadding();

        setupAppBar(getResources().getConfiguration());

        if (savedInstanceState == null) {
            showFragment(mCurrentFragmentIndex);
        }
    }

    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mAppBarLayout = findViewById(R.id.app_bar);
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mToolbar = findViewById(R.id.toolbar);
        mSwipeUpContainer = findViewById(R.id.swipe_up_container);
        mBottomContainer = findViewById(R.id.bottom_container);
        mDrawerListView = findViewById(R.id.drawer_list_view);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mDrawerLayout.setTitle(getString(R.string.app_name));
        // استخدم النص من strings.xml (يتبع لغة النظام)
        mDrawerLayout.setExpandedSubtitle(getString(R.string.app_subtitle));
    }

    private void setupDrawer() {
        initFragmentsList();

        // لا ننشئ DrawerLayout.DrawerListener لأن الـ inner class داخل المكتبة غير متاح خارجياً.
        // المكتبة تتعامل مع back press داخلياً، ويمكننا استخدام API العامة فقط.

        mDrawerListView.setLayoutManager(new LinearLayoutManager(this));
        mDrawerListView.setAdapter(
                mDrawerAdapter =
                        new DrawerListAdapter(
                                this,
                                mFragments,
                                position -> {
                                    if (position != mCurrentFragmentIndex) {
                                        mCurrentFragmentIndex = position;
                                        showFragment(position);
                                        // أغلق الـ drawer عبر API الخاص بـ One UI
                                        mDrawerLayout.setDrawerOpen(false, true);
                                        return true;
                                    }
                                    return false;
                                }));

        mDrawerAdapter.setSelectedItem(mCurrentFragmentIndex);
    }

    private void initFragmentsList() {
        mFragments.add(new HomeFragment());
        mFragments.add(new SettingsFragment());
    }

    private void showFragment(int position) {
        Fragment fragment = mFragments.get(position);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    private void setupAppBar(Configuration config) {
        ToolbarLayoutUtils.hideStatusBarForLandscape(this, config.orientation);
        ToolbarLayoutUtils.updateListBothSideMargin(this, mBottomContainer);

        if (config.orientation != Configuration.ORIENTATION_LANDSCAPE && !isInMultiWindowMode()) {
            mAppBarLayout.seslSetCustomHeightProportion(true, 0.5f);
            mEnableBackToHeader = true;
            mAppBarLayout.addOnOffsetChangedListener(new AppBarOffsetListener());
            mAppBarLayout.setExpanded(true, false);

            if (mSwipeUpContainer != null) {
                mSwipeUpContainer.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams lp = mSwipeUpContainer.getLayoutParams();
                lp.height = getResources().getDisplayMetrics().heightPixels / 2;
            }
        } else {
            mAppBarLayout.setExpanded(false, false);
            mEnableBackToHeader = false;
            mAppBarLayout.seslSetCustomHeightProportion(true, 0);
            if (mBottomContainer != null) {
                mBottomContainer.setAlpha(1f);
            }
            if (mSwipeUpContainer != null) {
                mSwipeUpContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupAppBar(newConfig);

        // أعد تطبيق padding بعد تغيير التكوين (مثلاً تغيير الاتجاه)
        applyDrawerTopPadding();
    }

    @Override
    public void onBackPressed() {
        // اترك إغلاق الـ drawer لمكتبة One UI (تسجل callback على OnBackPressedDispatcher داخلياً).
        // هنا فقط نتعامل مع مشكلة O كما في المثال الرسمي.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTaskRoot()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean isInMultiWindowMode() {
        return Build.VERSION.SDK_INT >= 24 && super.isInMultiWindowMode();
    }

    /**
     * يطبّق padding علوي للقائمة مساويًا لارتفاع الـ Toolbar.
     * يستخدم post() لضمان أن ارتفاع الـ toolbar قد تم قياسه.
     * يتضمن fallback إلى ?attr/actionBarSize إن لم يُقاس الارتفاع بعد.
     */
    private void applyDrawerTopPadding() {
        if (mToolbar == null || mDrawerListView == null) return;

        mToolbar.post(() -> {
            int top = mToolbar.getHeight();
            if (top <= 0) {
                top = getActionBarSize();
            }
            if (top > 0) {
                mDrawerListView.setPadding(0, top, 0, 0);
                mDrawerListView.setClipToPadding(false);
            }
        });
    }

    private int getActionBarSize() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }

    private class AppBarOffsetListener implements AppBarLayout.OnOffsetChangedListener {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            final int abs = Math.abs(verticalOffset);

            if (mSwipeUpContainer != null) {
                if (abs >= totalScrollRange / 2) {
                    mSwipeUpContainer.setAlpha(0f);
                } else if (abs == 0) {
                    mSwipeUpContainer.setAlpha(1f);
                } else {
                    float offsetAlpha = (appBarLayout.getY() / totalScrollRange);
                    float arrowAlpha = 1 - (offsetAlpha * -3);
                    arrowAlpha = Math.max(0, Math.min(1, arrowAlpha));
                    mSwipeUpContainer.setAlpha(arrowAlpha);
                }
            }

            if (mBottomContainer != null) {
                final float alphaRange = mCollapsingToolbar.getHeight() * 0.143f;
                final float layoutPosition = Math.abs(appBarLayout.getTop());
                float bottomAlpha =
                        (150.0f / alphaRange)
                                * (layoutPosition - (mCollapsingToolbar.getHeight() * 0.35f));

                bottomAlpha = Math.max(0, Math.min(255, bottomAlpha));
                mBottomContainer.setAlpha(bottomAlpha / 255);
            }
        }
    }
            }
