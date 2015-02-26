package com.yaldaco.daycalendar.CustomClasses;

import android.content.Context;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

/**
 * Created by Nasr_M on 2/15/2015.
 */
public class CustomScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    Context context;

    public CustomScaleGestureListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();

        if (scaleFactor > 1) {
            Toast.makeText(context, "Zooming In . . .", Toast.LENGTH_SHORT).show();
        } else if (scaleFactor < 1){
            Toast.makeText(context, "Zooming Out . . .", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
    }
}
