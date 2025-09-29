package com.example.oneuiapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Behavior آمن لـ AppBarLayout يجري فحوصات null قبل استدعاء السلوك الأصلي
 * ويمنع تحطم التطبيق لو مرر النظام قيمة null أو حدث استثناء غير متوقع داخل السلوك الأصلي.
 */
public class SafeAppBarBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "SafeAppBarBehavior";

    public SafeAppBarBehavior() {
        super();
    }

    public SafeAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // نسخة قديمة من onNestedScroll (موجودة في بعض نسخ المكتبة)
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                               View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (target == null) {
            Log.w(TAG, "onNestedScroll: target is null — skipping to avoid NPE");
            return;
        }
        try {
            super.onNestedScroll(coordinatorLayout, child, target,
                    dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        } catch (NullPointerException e) {
            // لا نرمي الاستثناء؛ فقط نسجل لتتبع السبب على أجهزة معينة
            Log.w(TAG, "Caught NPE from super.onNestedScroll — swallowing to avoid crash", e);
        }
    }

    // نسخة أحدث من onNestedScroll (مع معلمة type) لدعم إصدارات المكتبة المختلفة
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        if (target == null) {
            Log.w(TAG, "onNestedScroll(type): target is null — skipping to avoid NPE");
            return;
        }
        try {
            super.onNestedScroll(coordinatorLayout, child, target,
                    dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        } catch (NullPointerException e) {
            Log.w(TAG, "Caught NPE from super.onNestedScroll(type) — swallowing to avoid crash", e);
        }
    }
}
