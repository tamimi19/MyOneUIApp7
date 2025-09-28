package com.example.oneuiapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.widget.NestedScrollView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // collapsing toolbar
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.title_features));

        // apply fixed (non-attr) text appearances programmatically to avoid theme resolution errors
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedTitleFixed);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedTitleFixed);

        // spacing similar to One UI
        collapsingToolbar.setExpandedTitleMarginStart(dpToPx(16));
        collapsingToolbar.setExpandedTitleMarginBottom(dpToPx(16));

        // enable overscroll to allow dragging content down
        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll);
        if (nestedScrollView != null) {
            nestedScrollView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        }

        // Optional: add listener to react to toolbar offset changes
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // verticalOffset < 0 when collapsed
                    // You can add custom behavior here if needed
                }
            });
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
