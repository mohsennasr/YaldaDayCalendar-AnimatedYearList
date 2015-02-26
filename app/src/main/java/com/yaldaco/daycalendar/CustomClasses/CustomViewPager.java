package com.yaldaco.daycalendar.CustomClasses;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Nasr_M on 2/5/2015.
 */
public class CustomViewPager extends ViewPager {

    Context context;

    public CustomViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Toast.makeText(getContext(), "CustomViewPager InterceptTouchEvent . . .", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Toast.makeText(getContext(), "CustomViewPager TouchEvent . . .", Toast.LENGTH_SHORT).show();
        return false;
    }
}