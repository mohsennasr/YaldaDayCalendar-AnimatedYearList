package com.yaldaco.daycalendar.Utility;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.yaldaco.daycalendar.R;

import java.util.zip.Inflater;

/**
 * Created by Nasr_M on 1/31/2015.
 */
public class DatePickerDialog extends Fragment{

    Context context;
    NumberPicker yeardp, monthdp, daydp;
    int year, month, day;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.date_picker, container, false);

        NumberPicker yearDP = (NumberPicker) rootView.findViewById(R.id.dp1);
        NumberPicker monthDP = (NumberPicker) rootView.findViewById(R.id.dp2);
        NumberPicker dayDP = (NumberPicker) rootView.findViewById(R.id.dp3);

        String[] yearList = new String[]{"1391", "1392", "1393", "1394"};
        String[] monthList = new String[]{"month1", "month2", "month3", "month4"};
        String[] dayList = new String[]{"1", "2", "3", "4"};
        yearDP.setDisplayedValues(yearList);
        monthDP.setDisplayedValues(monthList);
        dayDP.setDisplayedValues(dayList);


        return rootView;
    }

    public void initial(){


    }
}
