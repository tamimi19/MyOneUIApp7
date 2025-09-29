package com.example.oneuiapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MainActivity extends AppCompatActivity {
    
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        
        // تعيين العنوان (سيظهر كبيرًا عند التمدد ومصغرًا عند الطي)
        collapsingToolbar.setTitle(getString(R.string.title_features));
        
        // إخفاء عنوان ActionBar لأن CollapsingToolbar سيتولى العرض
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        // مراقبة التمرير (اختياري - يمكن استخدامه لإضافة سلوك مخصص)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // verticalOffset سالب عند الطي
            }
        });
    }
}
