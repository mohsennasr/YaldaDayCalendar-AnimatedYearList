package com.yaldaco.daycalendar.YearCalendar;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaldaco.daycalendar.MonthCalendar.MonthUC;
import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;

import java.util.Calendar;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.getInstance;

/**
 * Created by Nasr_M on 2/1/2015.
 */
public class YearUC extends LinearLayout{

    public View rootView;
    public LinearLayout rootLayout;
    private Calendar yearCal = getInstance();
    private LinearLayout[] monthView;
    private TextView[] monthName;
    private TextView yearNum;
    private MonthUC monthCal;
    private Context context;

    public YearUC(Context context) {
        super(context);
        this.context = context;
    }

    public void init(){
        monthView = new LinearLayout[12];
        monthName = new TextView[12];
        monthCal = new MonthUC(context);
        yearNum = (TextView) rootView.findViewById(R.id.year_num);
        MyPersianCalendar myPersianCalendar = new MyPersianCalendar(yearCal);
        myPersianCalendar.persianSet(MONTH, 0);
        yearNum.setText(String.valueOf(myPersianCalendar.getiPersianYear()));
        Calendar yearMonthCal = getInstance();
        for(int i=0; i<12; i++) {
            myPersianCalendar.persianSet(MONTH, i);
            int id = rootView.getResources().getIdentifier("month_" + (i + 1), "id", context.getPackageName());
            monthView[i] = (LinearLayout) rootView.findViewById(id);
            id = rootView.getResources().getIdentifier("month_name_" + (i + 1), "id", context.getPackageName());
            monthName[i] = (TextView) rootView.findViewById(id);
            monthName[i].setText(myPersianCalendar.getPersianMonthName());
            monthName[i].setTextAppearance(context, R.style.text_size_2);
            yearMonthCal.setTime(myPersianCalendar.getMiladiDate().getTime());
            monthCal.threadStart(yearMonthCal, monthView[i]);
        }
    }

    private Runnable yearRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                init();
            }
        };
        return r;
    }

    class YearAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            init();
            return null;
        }
    }

    public void threadStart(Calendar yearCal, final LinearLayout rootLayout){

        this.yearCal.setTime(yearCal.getTime());
        this.rootLayout = rootLayout;

        rootView = inflate(context, R.layout.year_view, rootLayout);

//        Thread thread = new Thread(yearRunnable(), "YearUC Thread");
//        YearAsync yearAsync = new YearAsync();;

//        yearAsync.execute((Void[]) null);
//        thread.start();
        init();
    }
}
