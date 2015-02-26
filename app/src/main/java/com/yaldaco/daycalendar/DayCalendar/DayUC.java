package com.yaldaco.daycalendar.DayCalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yaldaco.daycalendar.Adapters.ListViewAdapter;
import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.ArabicDateConverter;
import com.yaldaco.daycalendar.Utility.MyDB;
import com.yaldaco.daycalendar.Utility.MyPersianCalendar;
import com.yaldaco.daycalendar.Utility.PersianUtil;

import java.util.ArrayList;
import java.util.Calendar;

import static android.graphics.Typeface.createFromAsset;

/**
 * Created by Nasr_M on 1/27/2015.
 */
public class DayUC extends LinearLayout{

    TextView dayName, dayDate, dayliNote1, dayliNote2, monthName, miladiFullDate, jalaliFulldate,
            arabicFullDate, miladiDate, jalaliDate, arabicdate, holyDayNote;
    String PDate[], ADate[], DateIndex;
    String[] MonthName = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    Typeface iranNastaliq, homa, arabic, times;
    Calendar baseCalendar = Calendar.getInstance();
    MyDB calendarDB;
    Context context;
    View rootView;
    LinearLayout rootLayout;
    ListView noteList;
    ListViewAdapter adapter;
    ArrayList<String> notes = new ArrayList<String>();

    public DayUC(Context context) {
        super(context);
        this.context = context;
    }

    //set calendar data
    public void putData() {
        ContentValues calendarContent, noteContents;

        //clear notes list for referesh
        notes.clear();

        //set date index based on calendar
        DateIndex = baseCalendar.get(Calendar.YEAR) + "" + (baseCalendar.get(Calendar.MONTH) + 1) + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);

        //read data from DB
        try {
            //read calendar data
            calendarContent = calendarDB.readData(DateIndex);

            //read notes
            noteContents = calendarDB.getNote(DateIndex);

            //set daily note
            dayliNote1.setText(calendarContent.getAsString(MyDB.COLUMN_VERSE_1));
            dayliNote2.setText(calendarContent.getAsString(MyDB.COLUMN_VERSE_2));

            //set holiday note
            holyDayNote.setText(calendarContent.getAsString(MyDB.COLUMN_HOLYDAY));

            //add notes to array list
            for(int i=0; i< noteContents.size() ; i++)
                notes.add(noteContents.getAsString(MyDB.COLUMN_NOTE + i));

            //update array list adapter
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get arabic date
        Calendar temporary = Calendar.getInstance();
        temporary.set(baseCalendar.get(Calendar.YEAR)
                , baseCalendar.get(Calendar.MONTH)
                , baseCalendar.get(Calendar.DATE));
        temporary.add(Calendar.DATE, -1);
        ADate = ArabicDateConverter.writeIslamicDate(temporary);

        //convert digits to persian digit
        MyPersianCalendar pcal = new MyPersianCalendar(baseCalendar);
        PDate = new String[5];
        PDate[0] = PersianUtil.convertDigits(String.valueOf(pcal.getiPersianYear()));
        PDate[1] = PersianUtil.convertDigits(String.valueOf(pcal.getiPersianMonth()));
        PDate[2] = PersianUtil.convertDigits(String.valueOf(pcal.getiPersianDate()));
        PDate[3] = pcal.getPersianMonthName();
        PDate[4] = pcal.getPersianDayName();

        //convert digits to arabic digits
        ADate[0] = PersianUtil.convertDigitstoArabic(ADate[0]);
        ADate[1] = PersianUtil.convertDigitstoArabic(ADate[1]);
        ADate[2] = PersianUtil.convertDigitstoArabic(ADate[2]);

        //set text color to RED for holidays
        if(PDate[4] == "جمعه"  || !holyDayNote.getText().toString().isEmpty()){
            monthName.setTextColor(Color.RED);
            dayName.setTextColor(Color.RED);
            dayDate.setTextColor(Color.RED);
            holyDayNote.setTextColor(Color.RED);
        }else{
            monthName.setTextColor(Color.BLACK);
            dayName.setTextColor(Color.BLACK);
            dayDate.setTextColor(Color.BLACK);
            holyDayNote.setTextColor(Color.BLACK);
        }

        //set calendar textViews
        dayName.setText(PDate[4]);
        dayDate.setText(PDate[2]);
        monthName.setText(PDate[3]);
        jalaliFulldate.setText(PDate[3] + " " + PDate[0]);
        jalaliDate.setText(PDate[2]);

        miladiFullDate.setText(baseCalendar.get(Calendar.YEAR) + " "
                + MonthName[baseCalendar.get(Calendar.MONTH)]);
        miladiDate.setText(baseCalendar.get(Calendar.DAY_OF_MONTH) + "");

        arabicFullDate.setText(ADate[3] + " " + ADate[0]);
        arabicdate.setText(ADate[2]);
    }

    //initial calendar
    private void Initializer() {

        //create DB object
        try {
            calendarDB = new MyDB(context);
            calendarDB.openDB();
//            calendarDB.pullAsync.execute();
            calendarDB.commentDBPull();
            calendarDB.setHD();
        }catch (Exception e){
            e.printStackTrace();
        }

        //set typefaces for text view fonts
        iranNastaliq = createFromAsset(context.getAssets(), "iran_nastaliq.ttf");
        homa = createFromAsset(context.getAssets(), "homa.ttf");
        arabic = createFromAsset(context.getAssets(), "arabic.ttf");
        times = createFromAsset(context.getAssets(), "times.ttf");

        holyDayNote = (TextView) rootView.findViewById(R.id.holyday_note_tv);
        holyDayNote.setTypeface(homa);
        holyDayNote.setTextColor(Color.BLACK);

        noteList = (ListView) rootView.findViewById(R.id.note_list_lv);
        adapter = new ListViewAdapter(context, notes, calendarDB, DateIndex);
        noteList.setAdapter(adapter);

        dayDate = (TextView) rootView.findViewById(R.id.date_tv);
        dayDate.setTypeface(homa);
        dayDate.setTextColor(Color.BLACK);
        dayliNote1 = (TextView) rootView.findViewById(R.id.dayli_note_1_tv);
        dayliNote1.setTypeface(homa);
        dayliNote1.setTextColor(Color.BLACK);
        dayliNote2 = (TextView) rootView.findViewById(R.id.dayli_note_2_tv);
        dayliNote2.setTypeface(homa);
        dayliNote2.setTextColor(Color.BLACK);
        dayName = (TextView) rootView.findViewById(R.id.day_name_tv);
        dayName.setTypeface(homa);
        dayName.setTextColor(Color.BLACK);
        monthName = (TextView) rootView.findViewById(R.id.month_name_tv);
        monthName.setTypeface(homa);
        monthName.setTextColor(Color.BLACK);
        jalaliFulldate = (TextView) rootView.findViewById(R.id.jalali_date_full_tv);
        jalaliFulldate.setTypeface(homa);
        jalaliDate = (TextView) rootView.findViewById(R.id.jalali_date_tv);
        jalaliDate.setTypeface(homa);
        jalaliFulldate.setTextColor(Color.WHITE);
        jalaliDate.setTextColor(Color.WHITE);
        arabicFullDate = (TextView) rootView.findViewById(R.id.arabic_date_full_tv);
        arabicFullDate.setTypeface(arabic);
        arabicFullDate.setTextColor(Color.WHITE);
        arabicdate = (TextView) rootView.findViewById(R.id.arabic_date_tv);
        arabicdate.setTypeface(arabic);
        arabicdate.setTextColor(Color.WHITE);
        miladiFullDate = (TextView) rootView.findViewById(R.id.miladi_date_full_tv);
        miladiFullDate.setTypeface(times);
        miladiFullDate.setTextColor(Color.WHITE);
        miladiDate = (TextView) rootView.findViewById(R.id.miladi_date_tv);
        miladiDate.setTypeface(times);
        miladiDate.setTextColor(Color.WHITE);
    }

    private Runnable dayRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                Initializer();
                putData();
            }
        };
        return r;
    }

    private Runnable dayUpdateRunnable() {

        Runnable r = new Runnable() {
            public void run() {
                putData();
            }
        };
        return r;
    }

    class DayAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rootView = inflate(context, R.layout.day_view, rootLayout);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Initializer();
            putData();
        }
    }

    public void startThread(Calendar cal, final LinearLayout rootLayout){
        this.baseCalendar.setTime(cal.getTime());

        this.rootLayout = rootLayout;

        //should run in UI Thread
//        Thread runOnUI = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        rootView = inflate(context, R.layout.day_view, rootLayout);
//                    }
//                });
//            }
//        });
//
//        runOnUI.start();
//
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Thread thread = new Thread(dayRunnable(), "Day Thread");
        DayAsync dayAsync = new DayAsync();

        dayAsync.execute((Void[]) null);

//        thread.start();
//        Initializer();
//        putData();
    }

    public void updateBaseCalendar(Calendar baseCalendar) {
        this.baseCalendar = baseCalendar;
        putData();
    }

    public void updateDay(int value){
        switch (value){
            case 1:
                calendarDB.insertData(DateIndex
                        , dayliNote1.getText().toString()
                        , dayliNote2.getText().toString()
                        , holyDayNote.getText().toString());
                baseCalendar.add(Calendar.DATE, 1);
                DateIndex = baseCalendar.get(Calendar.YEAR) + ""
                        + (baseCalendar.get(Calendar.MONTH) + 1 )
                        + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);
                putData();
                break;
            case -1:
                calendarDB.insertData(DateIndex
                        , dayliNote1.getText().toString()
                        , dayliNote2.getText().toString()
                        , holyDayNote.getText().toString());
                baseCalendar.add(Calendar.DATE, -1);
                DateIndex = baseCalendar.get(Calendar.YEAR) + ""
                        + (baseCalendar.get(Calendar.MONTH) + 1 )
                        + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);
                putData();
                break;
        }
    }

    public void updateNoteList(String note){
        notes.add(note);
        adapter.notifyDataSetChanged();
        calendarDB.insertNote(DateIndex, notes.size() - 1, note);
    }

    public void gotoToday(){
        //save calendar parameters to change date
        calendarDB.insertData(DateIndex
                , dayliNote1.getText().toString()
                , dayliNote2.getText().toString()
                , holyDayNote.getText().toString());

        //set calendar for today
        baseCalendar = Calendar.getInstance();

        //set date index
        DateIndex = baseCalendar.get(Calendar.YEAR) + ""
                + (baseCalendar.get(Calendar.MONTH) + 1)
                + "" + baseCalendar.get(Calendar.DAY_OF_MONTH);

        //update calendar data
        putData();
    }
}
