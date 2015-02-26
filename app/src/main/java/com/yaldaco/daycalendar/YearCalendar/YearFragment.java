package com.yaldaco.daycalendar.YearCalendar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.yaldaco.daycalendar.MainActivity;
import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;

import java.util.Calendar;

/**
 * Created by Nasr_M on 2/8/2015.
 */
public class YearFragment extends Fragment {

    private Calendar yearCal = Calendar.getInstance();
    private MyPersianCalendar pcal;
    private View rootView;
    private LinearLayout[] layouts;
    private LinearLayout yearList;
    private YearUC yearUC;
    private Thread yearThread;
    private YearAsync yearAsync;
    private int currentPosition = 1;
    private Animation slideInLeft, slideOutRight, slideInRight, slideOutLeft, fadeIn, fadeOut, zoomIn, zoomOut;
    private YearMonthList yearMonthList;

    Calendar nextCal = Calendar.getInstance();
    int changeValue;
    ProgressDialog progressDialog;
    public boolean YEAR_LIST = false;

    public static YearFragment newInstance(Calendar cal){
        YearFragment yearFragment = new YearFragment();
        yearFragment.yearCal.setTime(cal.getTime());
        yearFragment.layouts = new LinearLayout[3];
        yearFragment.pcal = new MyPersianCalendar(yearFragment.yearCal);
        return yearFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.year_frame, container, false);
        slideInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
        fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        zoomIn = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        zoomOut = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);
        layouts[0] = (LinearLayout) rootView.findViewById(R.id.year_previous);
//        layouts[0].setVisibility(View.INVISIBLE);
        layouts[1] = (LinearLayout) rootView.findViewById(R.id.year_current);
//        layouts[1].setVisibility(View.VISIBLE);
        layouts[2] = (LinearLayout) rootView.findViewById(R.id.year_next);
//        layouts[2].setVisibility(View.INVISIBLE);

        yearList = (LinearLayout) rootView.findViewById(R.id.year_month_list);

        yearUC = new YearUC(getActivity());
        yearMonthList = YearMonthList.newInstance(getActivity(), yearList, yearCal);
        yearList.setVisibility(View.INVISIBLE);

        yearThread = new Thread(yearRunnable(), "Current Year Thread");
        yearAsync = new YearAsync();

        yearThread.start();
//        yearAsync.execute((Void[]) null);
//        init();

        return rootView;
    }

    private void init(){
        yearUC.threadStart(yearCal, layouts[1]);
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(yearCal.getTime());
        nextCal.add(Calendar.YEAR, 1);
        yearUC.threadStart(nextCal, layouts[2]);
        Calendar preCal = Calendar.getInstance();
        preCal.setTime(yearCal.getTime());
        preCal.add(Calendar.YEAR, -1);
        yearUC.threadStart(preCal, layouts[0]);
        layouts[0].setVisibility(View.INVISIBLE);
        layouts[1].setVisibility(View.VISIBLE);
        layouts[2].setVisibility(View.INVISIBLE);

        if(MainActivity.progressDialog != null) {
            MainActivity.progressDialog.dismiss();
            MainActivity.progressDialog = null;
        }
    }

    private class YearAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            init();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            layouts[0].setVisibility(View.INVISIBLE);
            layouts[1].setVisibility(View.VISIBLE);
            layouts[2].setVisibility(View.INVISIBLE);        }
    }

    private Runnable yearRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                init();
            }
        };
        return r;
    }

    public void updateYear(int value){
//        Calendar nextCal = Calendar.getInstance();
//        yearCal.add(Calendar.YEAR, value);
//        nextCal.setTime(yearCal.getTime());
//        layouts[Math.abs(currentPosition%3)].startAnimation(slideOutRight);
//        layouts[Math.abs(currentPosition%3)].setVisibility(View.GONE);
//        currentPosition += value;
//        if(currentPosition < 0)
//            currentPosition = 2;
//        layouts[Math.abs(currentPosition%3)].setVisibility(View.VISIBLE);
//        layouts[Math.abs(currentPosition%3)].startAnimation(slideInLeft);
//        nextCal.add(Calendar.YEAR, value);
//        yearUC.threadStart(nextCal, layouts[(currentPosition + value) >= 0 ? (currentPosition + value)%3 : 2 ]);

        changeValue = value;

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("لطفاً منتظر بمانید...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        loadingAsync loading = new loadingAsync();
        loading.execute((Void[]) null);

    }

    public class loadingAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            yearCal.add(Calendar.YEAR, changeValue);
            yearMonthList.updateYear(yearCal);
            nextCal.setTime(yearCal.getTime());
            if (changeValue>0)
                layouts[Math.abs(currentPosition%3)].startAnimation(slideOutRight);
            else
                layouts[Math.abs(currentPosition%3)].startAnimation(slideOutLeft);
            layouts[Math.abs(currentPosition%3)].setVisibility(View.GONE);
            currentPosition += changeValue;
            if(currentPosition < 0)
                currentPosition = 2;
            nextCal.add(Calendar.YEAR, changeValue);
            yearUC.threadStart(nextCal, layouts[(currentPosition + changeValue) >= 0 ? (currentPosition + changeValue)%3 : 2 ]);
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            layouts[Math.abs(currentPosition%3)].setVisibility(View.VISIBLE);
            if(changeValue>0)
                layouts[Math.abs(currentPosition%3)].startAnimation(slideInLeft);
            else
                layouts[Math.abs(currentPosition%3)].startAnimation(slideInRight);

            MainActivity.progressDialog.dismiss();
            MainActivity.progressDialog = null;
        }
    }

    public void yearListView(String zoom){
//        layouts[currentPosition%3].setAnimation(fadeIn);

        if (zoom == "OUT" && yearList.getVisibility() == View.INVISIBLE){
            layouts[currentPosition % 3].startAnimation(fadeOut);
            layouts[currentPosition % 3].setVisibility(View.INVISIBLE);
            yearList.startAnimation(fadeIn);
            yearList.setVisibility(View.VISIBLE);
            YEAR_LIST = true;
        }
        else if (zoom == "IN" && yearList.getVisibility() == View.VISIBLE){
            yearList.startAnimation(fadeOut);
            yearList.setVisibility(View.INVISIBLE);
            layouts[currentPosition % 3].startAnimation(fadeIn);
            layouts[currentPosition % 3].setVisibility(View.VISIBLE);
            YEAR_LIST = false;
        }
    }
}
