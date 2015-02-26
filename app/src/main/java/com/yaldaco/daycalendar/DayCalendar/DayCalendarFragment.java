package com.yaldaco.daycalendar.DayCalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yaldaco.daycalendar.PersianDatePicker.PersianDatePicker;
import com.yaldaco.daycalendar.PersianDatePicker.Util.PersianCalendar;
import com.yaldaco.daycalendar.R;

import java.util.Calendar;

/**
 * Created by Nasr_M on 1/28/2015.
 */
public class DayCalendarFragment extends Fragment implements AdapterView.OnItemClickListener{

    Calendar baseCalendar = Calendar.getInstance();
    Context context;
    View rootView;
    LinearLayout[] layouts;
    private Animation slideInLeft, slideOutRight, slideInRight, slideOutLeft;
    private DayUC[] days;
    int currentPosition = 1;
    String DateIndex;
    ProgressDialog progressDialog;

    public static DayCalendarFragment newInstance(Calendar cal){        //create new instance of DayCalendar
        DayCalendarFragment dayFragment = new DayCalendarFragment();
        dayFragment.baseCalendar.setTime(cal.getTime());
        dayFragment.layouts = new LinearLayout[3];
        dayFragment.days = new DayUC[3];
//        dayFragment.progressDialog = new ProgressDialog(dayFragment.getActivity());
        return dayFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.day_frame, container, false);        //inflate root view of day calendar
        layouts[0] = (LinearLayout) rootView.findViewById(R.id.previous_day_frame);
        layouts[1] = (LinearLayout) rootView.findViewById(R.id.current_day_frame);
        layouts[2] = (LinearLayout) rootView.findViewById(R.id.next_day_frame);
        context = this.getActivity();

        slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);



        DateIndex = baseCalendar.get(Calendar.YEAR) + "" + (baseCalendar.get(Calendar.MONTH) + 1) + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);


        Thread thread = new Thread(dayRunnable(), "Day Thread");
        DayAsync dayAsync = new DayAsync();

        thread.start();

        return rootView;
    }

    private void init(){
        days[0] = new DayUC(context);
        days[1] = new DayUC(context);
        days[2] = new DayUC(context);

        Calendar nextCal, preCal;
        preCal = Calendar.getInstance();
        nextCal = Calendar.getInstance();
        nextCal.setTime(baseCalendar.getTime());
        preCal.setTime(baseCalendar.getTime());

        days[1].startThread(baseCalendar, layouts[1]);

        nextCal.add(Calendar.DATE, 1);
        days[2].startThread(nextCal, layouts[2]);

        preCal.add(Calendar.DATE, -1);
        days[0].startThread(preCal, layouts[0]);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layouts[0].setVisibility(View.INVISIBLE);
                layouts[1].setVisibility(View.VISIBLE);
                layouts[2].setVisibility(View.INVISIBLE);
            }
        });
        if(progressDialog != null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context, "Day Calendar Item Clicked . . .", Toast.LENGTH_SHORT).show();
    }

    private class DayAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            init();
            return null;
        }
    }

    private Runnable dayRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                init();
            }
        };
        return r;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addButtonClicked(){                         //add note button pressed

        Toast.makeText(context, "Add Button Clicked...", Toast.LENGTH_SHORT).show();

        //create dialog layout
        final EditText input = new EditText(context);
        input.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        final String[] value = {new String()};

        //create dialog
        AlertDialog.Builder inputNote = new AlertDialog.Builder(context);
        inputNote.setTitle("اضافه کردن یادداشت");
        inputNote.setView(input);

        //set dialog buttons
        inputNote.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value[0] = input.getText().toString();
                String tmp = value[0];
                DateIndex = baseCalendar.get(Calendar.YEAR)
                        + "" + (baseCalendar.get(Calendar.MONTH) + 1)
                        + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);
                if(!tmp.isEmpty()) {
                    days[currentPosition >= 0 ? currentPosition%3 : 2 ].updateNoteList(tmp);
                }
            }
        });
        inputNote.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        //show dialog
        inputNote.show();
    }

    public void todayButtonClicked(){       //today button pressed
        Toast.makeText(context, "Today Button Clicked . . .", Toast.LENGTH_SHORT).show();
        if(baseCalendar.compareTo(Calendar.getInstance()) != 0) {
            baseCalendar = Calendar.getInstance();

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("لطفاً منتظر بمانید...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Thread thread = new Thread(dayRunnable(), "Day Thread");
            thread.start();
        }
    }

    public void datePickerButtonSelected(){         //date picker button pressed
        Toast.makeText(context, "DatePicker Button Clicked . . .", Toast.LENGTH_SHORT).show();

        //create dialog
        AlertDialog.Builder datePicker = new AlertDialog.Builder(getActivity());

        //create persian date picker object and assign dates
        final PersianDatePicker persianDatePicker = new PersianDatePicker(getActivity());
        PersianCalendar pcal = new PersianCalendar(days[currentPosition%3].baseCalendar.getTimeInMillis());
        persianDatePicker.setDisplayPersianDate(pcal);
        datePicker.setView(persianDatePicker);

        //set date picker dialog buttons
        datePicker.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar cal = Calendar.getInstance();
                PersianCalendar tpcal = persianDatePicker.getDisplayPersianDate();
//                cal.setTime(tpcal.getTime());
                baseCalendar.setTime(tpcal.getTime());

                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("لطفاً منتظر بمانید...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Thread thread = new Thread(dayRunnable(), "Day Thread");
                thread.start();
//                days[currentPosition].updateBaseCalendar(cal);
            }
        });
        datePicker.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //show dialog
        datePicker.show();
    }

    public Calendar getBaseCalendar() {
        return baseCalendar;
    }

    public void updateDay(int value){
        Calendar nextCal = Calendar.getInstance();
        baseCalendar.add(Calendar.DATE, value);
        nextCal.setTime(baseCalendar.getTime());
        if (value == 1)
            layouts[Math.abs(currentPosition%3)].startAnimation(slideOutRight);
        else if(value == -1)
            layouts[Math.abs(currentPosition%3)].startAnimation(slideOutLeft);

        layouts[Math.abs(currentPosition%3)].setVisibility(View.GONE);
        currentPosition += value;
        layouts[Math.abs(currentPosition%3)].setVisibility(View.VISIBLE);

        if (value == 1)
            layouts[Math.abs(currentPosition%3)].startAnimation(slideInLeft);
        else if (value == -1)
            layouts[Math.abs(currentPosition%3)].startAnimation(slideInRight);

        nextCal.add(Calendar.DATE, value);
        days[Math.abs((currentPosition + value) % 3)].startThread(nextCal, layouts[Math.abs((currentPosition + value) % 3)]);
    }
}
