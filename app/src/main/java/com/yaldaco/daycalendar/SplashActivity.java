package com.yaldaco.daycalendar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Nasr_M on 2/9/2015.
 */
public class SplashActivity extends FragmentActivity {

//    MainActivity main;
//    public static RelativeLayout relativeLayout;
//    LinearLayout rootLayout;
    public static ProgressDialog progressDialog;
    Context context;

    float x1,x2,y1,y2;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); //Set Direction Right-to-Left

        context = this;

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle("لطفاً منتظر بمانید...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

//        rootLayout = (LinearLayout) findViewById(R.id.splash_root);
//        relativeLayout = (RelativeLayout) findViewById(R.id.splash_layout);

//        Handler handler = new Handler();
//
//        // run a thread after 2 seconds to start the home screen
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                // make sure we close the splash screen so the user won't come
//                // back when it presses back key
//                finish();
//                // start the home screen
//
//
//
//            }
//
//        }, 0);

        SplashASync splashASync = new SplashASync();
        splashASync.execute((Void[]) null);

//        MainInitAsync mainInit = new MainInitAsync();
//        mainInit.execute((Void[]) null);
    }

    public class SplashASync extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("لطفاً منتظر بمانید...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = new Intent(SplashActivity.this,
                    MainActivity.class);
            startActivity(intent);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            SplashActivity.this.finish();
        }
    }
}
