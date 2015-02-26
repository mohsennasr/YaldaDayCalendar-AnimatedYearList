package com.yaldaco.daycalendar.Utility;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mohsen on 18/12/2014.
 */
public class AccessDB {
    Scanner read;
    ContentValues cv1, cv2;
    Context con;

    public AccessDB(Context con) {
        this.con = con;
        cv1 = new ContentValues();
        cv2 = new ContentValues();
    }

    public void readFile(){
        Integer i = 1;

        AssetManager am = con.getAssets();

        try {
            InputStream is = am.open("data.csv");
            read = new Scanner(is);
            read.useDelimiter(",");
            for (i = 1; i <= 400; i++){
                cv2.put(i.toString(), read.next());
                cv1.put(i.toString(),read.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        am.close();
    }
}
