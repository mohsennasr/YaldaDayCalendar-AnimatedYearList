package com.yaldaco.daycalendar.MonthCalendar;

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
import android.widget.TextView;

import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;

import java.util.Calendar;

/**
 * Created by Nasr_M on 2/3/2015.
 */
public class MonthCalendarFragment extends Fragment {

    private View rootView;
    private LinearLayout[] monthLayouts, yearLayouts;
    private TextView yearNum, monthName;

    private Calendar monthCal = Calendar.getInstance();
    private MyPersianCalendar pcal;
    private MonthUC monthUC;

    private Animation slideInLeft, slideOutRight, slideInRight, slideOutLeft;

    private Thread monthInitThread, yearInitThread;
    private MonthAsync monthAsync;
    private int currentPosition = 1;
    private int changedValue;

    public static MonthCalendarFragment newInstance(Calendar cal){
        MonthCalendarFragment monthCalendarFragment = new MonthCalendarFragment();
        monthCalendarFragment.monthCal.setTime(cal.getTime());
        monthCalendarFragment.monthLayouts = new LinearLayout[3];
        monthCalendarFragment.yearLayouts = new LinearLayout[2];
        monthCalendarFragment.pcal = new MyPersianCalendar(monthCalendarFragment.monthCal);
        return monthCalendarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.month_frame, container, false);
        slideInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);

        monthLayouts[0] = (LinearLayout) rootView.findViewById(R.id.month_frame_previous_month);
        monthLayouts[0].setVisibility(View.INVISIBLE);
        monthLayouts[1] = (LinearLayout) rootView.findViewById(R.id.month_frame_current);
        monthLayouts[1].setVisibility(View.VISIBLE);
        monthLayouts[2] = (LinearLayout) rootView.findViewById(R.id.month_frame_next_month);
        monthLayouts[2].setVisibility(View.INVISIBLE);

        yearLayouts[0] = (LinearLayout) rootView.findViewById(R.id.month_frame_previous_year);
        yearLayouts[0].setVisibility(View.INVISIBLE);
        yearLayouts[1] = (LinearLayout) rootView.findViewById(R.id.month_frame_next_year);
        yearLayouts[1].setVisibility(View.INVISIBLE);

        monthUC = new MonthUC(getActivity());

        monthName = (TextView) rootView.findViewById(R.id.month_month_name);
        yearNum = (TextView) rootView.findViewById(R.id.month_year_num);

        monthName.setText(pcal.getPersianMonthName());
        yearNum.setText(String.valueOf(pcal.getiPersianYear()));

        monthInitThread = new Thread(monthRunnable(), "Month Thread");
        yearInitThread = new Thread(monthYearRunnable(), "Month Year Thread");
//        monthAsync = new MonthAsync();

        monthInitThread.start();
        yearInitThread.start();
//        monthAsync.execute((Void[]) null);

        return rootView;
    }

    private void init(){
        Calendar nextCal, preCal;
        nextCal =Calendar.getInstance();
        preCal = Calendar.getInstance();
        nextCal.setTime(monthCal.getTime());
        preCal.setTime(monthCal.getTime());

        monthUC.threadStart(monthCal, monthLayouts[1]);

        nextCal.add(Calendar.MONTH, 1);
        monthUC.threadStart(nextCal, monthLayouts[2]);

        preCal.add(Calendar.MONTH, -1);
        monthUC.threadStart(preCal, monthLayouts[0]);
    }

    private void yearInit() {
        Calendar nextCal, preCal;
        nextCal =Calendar.getInstance();
        preCal = Calendar.getInstance();
        nextCal.setTime(monthCal.getTime());
        preCal.setTime(monthCal.getTime());

        nextCal.add(Calendar.YEAR, 1);
        monthUC.threadStart(nextCal, yearLayouts[1]);

        preCal.add(Calendar.YEAR, -1);
        monthUC.threadStart(preCal, yearLayouts[0]);
    }

    private class MonthAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            init();
            return null;
        }
    }

    private Runnable monthRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                init();
            }
        };
        return r;
    }

    private Runnable monthYearRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                yearInit();
            }
        };
        return r;
    }

    private class MonthUpdateAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            updateMonth();
            return null;
        }
    }

    private Runnable monthUpdateRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                updateMonth();
            }
        };
        return r;
    }

    public void updateMonth(){
        Calendar nextCal = Calendar.getInstance();
        monthCal.add(Calendar.MONTH, changedValue);
        pcal.setMiladiDate(monthCal);
        monthName.setText(pcal.getPersianMonthName());
        yearNum.setText(String.valueOf(pcal.getiPersianYear()));

        nextCal.setTime(monthCal.getTime());
        if (changedValue == 1)
            monthLayouts[Math.abs(currentPosition%3)].startAnimation(slideOutRight);
        else if (changedValue == -1)
            monthLayouts[Math.abs(currentPosition%3)].startAnimation(slideOutLeft);
        monthLayouts[Math.abs(currentPosition%3)].setVisibility(View.GONE);
        currentPosition += changedValue;
        monthLayouts[Math.abs(currentPosition%3)].setVisibility(View.VISIBLE);
        if (changedValue == 1)
            monthLayouts[Math.abs(currentPosition%3)].startAnimation(slideInLeft);
        else if (changedValue == -1)
            monthLayouts[Math.abs(currentPosition%3)].startAnimation(slideInRight);
        nextCal.add(Calendar.MONTH, changedValue);
        monthUC.threadStart(nextCal, monthLayouts[Math.abs((currentPosition + changedValue) % 3)]);
    }

    public void updateThreadStart(int changedValue){
        this.changedValue = changedValue;
        Thread updateThread = new Thread(monthUpdateRunnable(), "Update Month Thread");
        MonthUpdateAsync monthUpdateAsync = new MonthUpdateAsync();

//        updateThread.start();
//        monthUpdateAsync.execute((Void[]) null);
        updateMonth();
    }
}
