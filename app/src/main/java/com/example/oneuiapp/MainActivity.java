package com.example.oneuiapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.sesl.widget.SeslToolbar;
import androidx.sesl.appbar.SeslCollapsingToolbarLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // استدعاء شريط الأدوات الجديد من SESL وتعيينه كـ ActionBar
        SeslToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // استدعاء CollapsingToolbarLayout الجديد من SESL وتعيين العنوان والعنوان الفرعي
        SeslCollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("عنوان التطبيق");
        collapsingToolbar.seslSetSubtitle("العنوان الفرعي");
    }
}
