package com.example.order_eckn2015;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

public class AutofitTabLayout extends TabLayout {
    public AutofitTabLayout(@NonNull Context context) {
        super(context);
    }

    public AutofitTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutofitTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (getTabCount() == 0)
                return;
            Field field = TabLayout.class.getDeclaredField(String.valueOf(widthMeasureSpec));//("mTabMinWidth");
            field.setAccessible(true);
            field.set(this, (int) (getMeasuredWidth() / (float) getTabCount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
