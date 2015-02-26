package com.yaldaco.daycalendar.CustomClasses;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Created by Nasr_M on 2/9/2015.
 */
public class CustomDrawer extends DrawerLayout {

    public CustomDrawer(Context context) {
        super(context);
    }

    public CustomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Toast.makeText(getContext(), "Custom Drawer InterceptTouchListener . . .", Toast.LENGTH_SHORT).show();
//        return false;
//    }
}
