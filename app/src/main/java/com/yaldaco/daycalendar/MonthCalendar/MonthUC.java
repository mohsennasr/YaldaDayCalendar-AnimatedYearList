package com.yaldaco.daycalendar.MonthCalendar;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaldaco.daycalendar.Adapters.GridViewAdapter;
import com.yaldaco.daycalendar.CustomClasses.CustomGridView;
import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Calendar.getInstance;

/**
 * Created by Nasr_M on 2/1/2015.
 */
public class MonthUC extends LinearLayout{

    private Thread thread;
    private MonthAsync monthAsync;
    private CustomGridView gridView;                //gridView
    private ArrayList<String> gridList;             //gridView content arrayList
    private Context context;                        //context
    private Calendar monthCal = getInstance();                      //calendar of this currentMonth
    private MyPersianCalendar persianCal;             //persian calendar of this currentMonth
    private LinearLayout rootLayout;
    private View rootView;
    private TextView monthName, year;

    private String[] dayNames = new String[]{"ش", "ی", "د", "س", "چ", "پ", "ج"};    //week days name

    public MonthUC(Context context) {
        super(context);
        this.context = context;
    }

    public void Initialize(){

        int maxDayInMonth, monthRemainingDay, firstWeekOfMonth, weekAssigned;

//        monthName.setText(persianCal.getPersianMonthName());
//        year.setText(String.valueOf(persianCal.getiPersianYear()));

        //get maximum day in currentMonth
        maxDayInMonth = persianCal.getMaxDayOfMonth();

        //calculate remaining day of previous currentMonth
        monthRemainingDay = persianCal.persianPreMonthRemainingDay();

        //add week day name header to grid
        gridList.add("");
        for(int i=0; i< 7; i++)
            gridList.add(dayNames[i]);

        //add first currentMonth week number
        firstWeekOfMonth = persianCal.getFirstWeekNumberOfMonth();
        gridList.add(String.valueOf(firstWeekOfMonth));
        firstWeekOfMonth++;
        weekAssigned = 0;

        //add empty cells for previous currentMonth remaining day
        for(int i=0; i<monthRemainingDay; i++)
            gridList.add("");

        if (firstWeekOfMonth > 53)
            firstWeekOfMonth = 1;

        //add currentMonth's week number and days
        for (int i=1; i <= maxDayInMonth;  ) {
            if (((i+monthRemainingDay+weekAssigned) % 8) == 0) {
                gridList.add(String.valueOf(firstWeekOfMonth));
                firstWeekOfMonth++;
                weekAssigned++;
                gridList.add(String.valueOf(i));
                i++;
            } else {
                gridList.add(String.valueOf(i));
                i++;
            }
        }

        GridViewAdapter gridViewAdapter = new GridViewAdapter(context, gridList);
        gridView.setAdapter(gridViewAdapter);                           //set gridview adapter

        //notify grid adapter for data changes
        gridViewAdapter.notifyDataSetChanged();
    }

    private Runnable monthRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                Initialize();
            }
        };
        return r;
    }

    class MonthAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            Initialize();
            return null;
        }
    }

    public void threadStart(Calendar cal, final LinearLayout rootLayout){
        monthAsync = new MonthAsync();
        thread = new Thread(monthRunnable(), "Month Thread");

        this.monthCal.setTime(cal.getTime());
        this.rootLayout = rootLayout;


//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        rootView = inflate(context, R.layout.month_view, rootLayout);
//                    }
//                });
//            }
//        }).start();

        persianCal = new MyPersianCalendar(this.monthCal);

//        monthName = (TextView) rootView.findViewById(R.id.month_frame_name);
//        year = (TextView) rootView.findViewById(R.id.month_frame_year);

        gridView = (CustomGridView) rootView.findViewById(R.id.month_grid);      //assign gridview
        gridList = new ArrayList<String>();                             //construct grid array list
        gridList.clear();

//        monthAsync.execute((Void[]) null);
//        thread.start();
        Initialize();
    }
}
