package com.yaldaco.daycalendar;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.yaldaco.daycalendar.Adapters.ExpListAdapter;
import com.yaldaco.daycalendar.CustomClasses.CustomDrawer;
import com.yaldaco.daycalendar.CustomClasses.CustomScaleGestureListener;
import com.yaldaco.daycalendar.CustomClasses.CustomViewPager;
import com.yaldaco.daycalendar.DayCalendar.DayCalendarFragment;
import com.yaldaco.daycalendar.MonthCalendar.MonthCalendarFragment;
import com.yaldaco.daycalendar.Utility.ExpListPreparation;
import com.yaldaco.daycalendar.YearCalendar.YearFragment;

import java.util.Calendar;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener, View.OnClickListener{

    //ExpList
    public ExpListAdapter expAdapter;          //expandable list adapter
    public ExpandableListView expListView;     //expandable list view for notification panel
//    private DrawerLayout drawerLayout;                  //drawer layout for notification panel
    public CustomDrawer drawerLayout;
    public ActionBarDrawerToggle drawerToggle;

    public DayCalendarFragment todayCal;
    public YearFragment yearCal;
    public MonthCalendarFragment monthCal, nextMonth, preMonth;

    //Tabs
//    private CustomViewPager customViewPager;
    public CustomViewPager customViewPager;
    public ActionBar actionBar;
    public TabPagerAdapter tabPagerAdapter;
    private String[] tabNames = new String[]{"تقویم روزانه", "تقویم ماهیانه", "تقویم سالیانه"};
    public static Calendar cal = Calendar.getInstance();
    public boolean drawerOpen = false;

    public static ProgressDialog progressDialog;
    private ScaleGestureDetector SGD;

//    FragmentActivity context;
//    float startPoint[0], endPoint[0], startPoint[1], endPoint[1];
    float[] startPoint, endPoint, distance;
    boolean TOUCH_ACTION_MOVE = false, IS_IN_VIEWPAGER_AREA = false, SWIPE_ACTION = true;
    String CURRENT = "Current", NEXT = "Next", PREVIOUS = "Previous", STATUS;

    Point point = new Point();

//    LinearLayout rootLayout;
//    View mainView;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); //Set Direction Right-to-Left

        MainInitAsync mainInit = new MainInitAsync();
        mainInit.execute((Void[]) null);

        init();
    }

    public void init() {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        startPoint = new float[4];
        endPoint = new float[4];
        distance = new float[2];


        STATUS = CURRENT;

//        MainInitAsync initAsync = new MainInitAsync();
//        initAsync.execute((Void[]) null);

/*
 *      setting up notification panel
 */
        //Initial and Configure ExpandableList
        //Assign ExpandableList to Layout
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        //Prepare ExpandableList Parent and Child Items
        ExpListPreparation expListData = new ExpListPreparation();
        //ExpandableList Adapter
        expAdapter = new ExpListAdapter(this, expListData.getParentList(), expListData.getChildList());
        expListView.setAdapter(expAdapter);

        //Assign Drawer to Layout
        drawerLayout = (CustomDrawer) findViewById(R.id.drawer_layout);

        //cofig actionBar to display home button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //initial drawer toggle and set open/close title
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, //Drawer Toggle Icon
                R.string.app_name, // Drawer Title - When Open
                R.string.app_name // Drawer Title - When Close
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
                // calling onPrepareOptionsMenu() to show action bar icons
                drawerOpen = false;
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("پیام ها");
                // calling onPrepareOptionsMenu() to hide action bar icons
                drawerOpen = true;
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.requestDisallowInterceptTouchEvent(false);
/*
 *      setting up tabs
 */
        //assign viewpager
        customViewPager = (CustomViewPager) findViewById(R.id.view_pager);

        //declare page offset limit in view pager
        customViewPager.setOffscreenPageLimit(3);

        //assign action bar
        actionBar = getActionBar();

        //construct tab pager adapter
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        //set view pager adapter
        customViewPager.setAdapter(tabPagerAdapter);

        //set action bar configurations
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //add tabs to action bar
        for (String tab_name : tabNames) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        SGD = new ScaleGestureDetector(this, new CustomScaleGestureListener(this));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        customViewPager.setCurrentItem(tab.getPosition());              //set view pager content base on selected tab
        invalidateOptionsMenu();      //invalidate option menu to set meu items based on current tab
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "Main Clicked . . .", Toast.LENGTH_SHORT).show();
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return todayCal;    // DayCalendarFragment.newInstance(cal);
                case 1:
                    return monthCal;
                case 2:
                    return yearCal;     // YearCalendarFragment.newInstance(cal);
//                    return new Fragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add items to the action bar if it is present.

        if (customViewPager.getCurrentItem() == 0) {
            getMenuInflater().inflate(R.menu.today_tab_menu, menu);
        } else
            getMenuInflater().inflate(R.menu.menu_main, menu);

        //if drawer is open hide all menu items
        if (drawerOpen)
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                todayCal.addButtonClicked();    //add day button clicked
                return true;
            case R.id.action_today:
                todayCal.todayButtonClicked();  //today button clicked
                return true;
            case R.id.action_date_picker:       //datePicker button clicked
                todayCal.datePickerButtonSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (drawerOpen)
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        String swipeDirection = "";
        if (customViewPager.getCurrentItem() == 2 && event.getPointerCount() >= 2) {
            SWIPE_ACTION = false;
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                startPoint[0] = event.getX(0);
                startPoint[1] = event.getY(0);
                startPoint[2] = event.getX(1);
                startPoint[3] = event.getY(1);
            }else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
                endPoint[0] = event.getX(0);
                endPoint[1] = event.getY(0);
                endPoint[2] = event.getX(1);
                endPoint[3] = event.getY(1);

                //start distance
                distance[0] = (float) Math.sqrt(Math.pow ((startPoint[0] - startPoint[2]), 2)
                        + Math.pow ((startPoint[1] - startPoint[3]), 2));

                //end distance
                distance[1] = (float) Math.sqrt(Math.pow ((endPoint[0] - endPoint[2]), 2)
                        + Math.pow ((endPoint[1] - startPoint[3]), 2));

                if (distance[0] < distance[1]) { //zoom in
                    //change view to show year list
                    yearCal.yearListView("IN");
                }else if (distance[0] > distance[1]) { //zoom out
                    //change view to show current year
                    yearCal.yearListView("OUT");
                }
            }

//            SGD.onTouchEvent(event);
//            return true;
        }
        if (SWIPE_ACTION)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getWindowManager().getDefaultDisplay().getSize(point);
                    if ((event.getX() > point.x - 40) || (event.getY() <= getActionBar().getHeight()) || drawerOpen) {
                        IS_IN_VIEWPAGER_AREA = false;
                        return super.dispatchTouchEvent(event);
                    } else
                        IS_IN_VIEWPAGER_AREA = true;
                    startPoint[0] = event.getX();
                    startPoint[1] = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!IS_IN_VIEWPAGER_AREA)
                        return super.dispatchTouchEvent(event);
                    endPoint[0] = event.getX();
                    endPoint[1] = event.getY();
                    float dx, dy;
                    dx = Math.abs(endPoint[0] - startPoint[0]);
                    dy = Math.abs(endPoint[1] - startPoint[1]);

                    if ((dx >= dy) && (startPoint[0] >= endPoint[0])) {
                        swipeDirection = "R2L";
                    } else if ((dx > dy) && (startPoint[0] < endPoint[0])) {
                        swipeDirection = "L2R";
                    } else if ((dx < dy) && (startPoint[1] >= endPoint[1])) {
                        swipeDirection = "D2U";
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    } else if ((dx < dy) && (startPoint[1] < endPoint[1])) {
                        swipeDirection = "U2D";
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                    if (dx > 10) {
                        startPoint[0] = endPoint[0] = startPoint[1] = endPoint[1] = 0;
                        switch (customViewPager.getCurrentItem()) {
                            case 0:     //Day Tab
                                TOUCH_ACTION_MOVE = false;
                                if (swipeDirection == "R2L") {
                                    cal.add(Calendar.DATE, 1);
                                    todayCal.updateDay(1);
                                } else if (swipeDirection == "L2R") {
                                    cal.add(Calendar.DATE, -1);
                                    todayCal.updateDay(-1);
                                }
                                break;
                            case 1:     //Month Tab
                                TOUCH_ACTION_MOVE = false;
                                if (swipeDirection == "R2L") {
                                    cal.add(Calendar.MONTH, 1);
                                    monthCal.updateThreadStart(1);
                                } else if (swipeDirection == "L2R") {
                                    cal.add(Calendar.MONTH, -1);
                                    monthCal.updateThreadStart(-1);
                                } else if (swipeDirection == "D2U") {
                                    cal.add(Calendar.YEAR, 1);
                                    monthCal.updateThreadStart(1);
                                } else if (swipeDirection == "U2D") {
                                    cal.add(Calendar.YEAR, -1);
                                    monthCal.updateThreadStart(-1);
                                }
                                break;
                            case 2:     //Year Tab
                                if (!yearCal.YEAR_LIST) {
                                    TOUCH_ACTION_MOVE = false;
                                    if (swipeDirection == "R2L") {
                                        cal.add(Calendar.YEAR, 1);
                                        yearCal.updateYear(1);
                                    } else if (swipeDirection == "L2R") {
                                        cal.add(Calendar.YEAR, -1);
                                        yearCal.updateYear(-1);
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (customViewPager.getCurrentItem() == 2
                            && Math.abs(event.getX() - startPoint[0]) > 10
                            && Math.abs(event.getY() - startPoint[1]) < Math.abs(event.getY() - startPoint[0])
                            && progressDialog == null
                            && IS_IN_VIEWPAGER_AREA
                            && SWIPE_ACTION
                            && !yearCal.YEAR_LIST) {
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("لطفاً منتظر بمانید...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    break;
            }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            SWIPE_ACTION = true;
        }
        return super.dispatchTouchEvent(event);
    }

    public class MainInitAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //construct calendars class
            todayCal = DayCalendarFragment.newInstance(cal);
            monthCal = MonthCalendarFragment.newInstance(cal);
            cal.add(Calendar.MONTH, 1);
            nextMonth = MonthCalendarFragment.newInstance(cal);
            cal.add(Calendar.MONTH, -2);
            preMonth = MonthCalendarFragment.newInstance(cal);
            cal.add(Calendar.MONTH, 1);
            yearCal = YearFragment.newInstance(cal);
            return null;
        }
    }

}
