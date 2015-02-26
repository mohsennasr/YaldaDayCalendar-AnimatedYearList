package com.yaldaco.daycalendar.YearCalendar;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.yaldaco.daycalendar.Adapters.YearGridAdapter;
import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nasr_M on 2/14/2015.
 */
public class YearMonthList extends LinearLayout{

    private Context context;
    private LinearLayout rootLayout;
    private View rootView;
    private GridView yearGrid;
    private Calendar currentCal = Calendar.getInstance();
    private ArrayList<String> gridList;
    YearGridAdapter yearGridAdapter;

    public YearMonthList(Context context) {
        super(context);
    }

    public static YearMonthList newInstance(Context context, LinearLayout rootLayout, Calendar cal){
        YearMonthList yearMonthList = new YearMonthList(context);
        yearMonthList.context = context;
        yearMonthList.rootLayout = rootLayout;
        yearMonthList.currentCal.setTime(cal.getTime());
        yearMonthList.init();
        return yearMonthList;
    }

    private void init(){
        int year;
        rootView = inflate(context, R.layout.year_list, rootLayout);
        yearGrid = (GridView) rootView.findViewById(R.id.year_grid_list);
        gridList = new ArrayList<>();
        yearGridAdapter = new YearGridAdapter(context, gridList);
        MyPersianCalendar pCal = new MyPersianCalendar(currentCal);
        year = pCal.getiPersianYear();
        year = year -4;

        for (int i = 0; i< 9; i++ ){
            gridList.add(String.valueOf(year));
            year++;
        }
        yearGrid.setAdapter(yearGridAdapter);
        yearGridAdapter.notifyDataSetChanged();
    }

    public void updateYear(Calendar cal){
        int year;
        gridList.clear();
        currentCal.setTime(cal.getTime());
        MyPersianCalendar pCal = new MyPersianCalendar(currentCal);
        year = pCal.getiPersianYear();
        year = year -4;

        for (int i = 0; i< 9; i++ ){
            gridList.add(String.valueOf(year));
            year++;
        }
        yearGridAdapter.notifyDataSetChanged();
    }

}
