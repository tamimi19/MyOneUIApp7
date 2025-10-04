package com.example.oneuiapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import dev.oneuiproject.oneui.layout.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
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
        initFragmentsList(savedInstanceState);
        setupDrawer();
        
        if (savedInstanceState != null) {
            mCurrentFragmentIndex = savedInstanceState.getInt("current_fragment", 0);
        } else {
            showFragment(mCurrentFragmentIndex);
        }
    }

    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerListView = findViewById(R.id.drawer_list_view);
    }

    private void initFragmentsList(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                    .findFragmentByTag("fragment_0");
            SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager()
                    .findFragmentByTag("fragment_1");
            
            if (homeFragment != null) {
                mFragments.add(homeFragment);
            } else {
                mFragments.add(new HomeFragment());
            }
            
            if (settingsFragment != null) {
                mFragments.add(settingsFragment);
            } else {
                mFragments.add(new SettingsFragment());
            }
        } else {
            mFragments.add(new HomeFragment());
            mFragments.add(new SettingsFragment());
        }
    }

    private void setupDrawer() {
        mDrawerListView.setLayoutManager(new LinearLayoutManager(this));
        mDrawerListView.setAdapter(
                mDrawerAdapter = new DrawerListAdapter(
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment, "fragment_" + position)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_fragment", mCurrentFragmentIndex);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTaskRoot()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }
}
