package com.example.oneuiapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.title_features));

        // ضبط أنماط العنوان برمجياً (تجنب قراءة السمات أثناء نفخ الـ XML)
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedTitle);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedTitle);

        collapsingToolbar.setExpandedTitleMarginStart(dpToPx(16));
        collapsingToolbar.setExpandedTitleMarginBottom(dpToPx(16));
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
