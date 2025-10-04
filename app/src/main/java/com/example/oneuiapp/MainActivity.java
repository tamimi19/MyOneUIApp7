package com.example.oneuiapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SettingsHelper.wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        initFragmentsList();
        setupDrawer();
        
        if (savedInstanceState != null) {
            mCurrentFragmentIndex = savedInstanceState.getInt("current_fragment", 0);
            restoreFragments();
        } else {
            showFragment(mCurrentFragmentIndex);
        }
        
        setupAppBar(getResources().getConfiguration());
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
        mDrawerLayout.setExpandedSubtitle(getString(R.string.app_subtitle));
    }

    private void initFragmentsList() {
        if (mFragments.isEmpty()) {
            mFragments.add(new HomeFragment());
            mFragments.add(new SettingsFragment());
        }
    }

    private void setupDrawer() {
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
                                        mDrawerLayout.setDrawerOpen(false, true);
                                        return true;
                                    }
                                    return false;
                                }));

        mDrawerAdapter.setSelectedItem(mCurrentFragmentIndex);
    }

    private void showFragment(int position) {
        Fragment fragment = mFragments.get(position);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.main_content, fragment, "fragment_" + position)
                .commitNow();
        
        updateAppBarVisibility();
        
        if (!(fragment instanceof SettingsFragment)) {
            Configuration config = getResources().getConfiguration();
            if (config.orientation != Configuration.ORIENTATION_LANDSCAPE && !isInMultiWindowMode()) {
                mAppBarLayout.setExpanded(true, false);
            }
        }
    }

    private void restoreFragments() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if (fragment != null) {
            updateAppBarVisibility();
        } else {
            showFragment(mCurrentFragmentIndex);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_fragment", mCurrentFragmentIndex);
    }

    private void setupAppBar(Configuration config) {
        ToolbarLayoutUtils.hideStatusBarForLandscape(this, config.orientation);
        ToolbarLayoutUtils.updateListBothSideMargin(this, mBottomContainer);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        
        if (currentFragment instanceof SettingsFragment) {
            return;
        }

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

    private void updateAppBarVisibility() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);

        if (currentFragment instanceof SettingsFragment) {
            mAppBarLayout.setExpanded(false, false);
            mAppBarLayout.setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getString(R.string.title_settings));
            }
        } else {
            mAppBarLayout.setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupAppBar(newConfig);
        updateAppBarVisibility();
    }

    @Override
    public void onBackPressed() {
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
