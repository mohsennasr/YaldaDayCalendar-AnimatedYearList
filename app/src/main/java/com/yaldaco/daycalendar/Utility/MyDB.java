package com.yaldaco.daycalendar.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Mohsen on 15/12/2014.
 */
public class MyDB {

    public static final String TABLE_DIARY_NAME = "Diary";
    public static final String TABLE_COMMENT_NAME = "Comment";
    public static final String TABLE_NOTES_NAME = "Notes";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_NOTE = "NOTE";
    public static final String COLUMN_ITEM_ID = "ITEM_ID";
    public static final String COLUMN_HOLYDAY = "HOLYDAY";

    public static final String COLUMN_VERSE_1 = "VERSE1";
    public static final String COLUMN_VERSE_2 = "VERSE2";

    public static final String COLUMN_COMMENT_3 = "STATUS";

    private static final String SQL_CREATE_DIARY_ENTRIES = "CREATE TABLE " +
            TABLE_DIARY_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT, " +
//            COLUMN_NOTE + " TEXT DEFAULT ''," +
            COLUMN_VERSE_1 + " TEXT DEFAULT ''," +
            COLUMN_VERSE_2 + " TEXT DEFAULT ''," +
            COLUMN_HOLYDAY + " TEXT DEFAULT ''" + ");";
    private static final String SQL_CREATE_COMMENT_ENTRIES = "CREATE TABLE " +
            TABLE_COMMENT_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_VERSE_1 + " TEXT, " +
            COLUMN_VERSE_2 + " TEXT," +
            COLUMN_COMMENT_3 + " TEXT" + ");";
    private static final String SQL_CREATE_NOTES_ENTRIES = "CREATE TABLE " +
            TABLE_NOTES_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_ITEM_ID + " TEXT, " +
            COLUMN_NOTE + " TEXT " + ");";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "DailyCalendar12.db";
    private static final String SQL_DELETE_DIARY_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_DIARY_NAME;
    private static final String SQL_DELETE_COMMENT_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_COMMENT_NAME;
    private static final String SQL_DELETE_NOTES_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NOTES_NAME;

    private final Context dbcontext;
    private SQLiteDatabase dbsql;
    private DBHelper dbsqlhelper;

    public MyDB(Context c) {
        dbcontext = c;
    }

    public MyDB openDB() throws SQLException {
        dbsqlhelper = new DBHelper(dbcontext);
        dbsql = dbsqlhelper.getWritableDatabase();
        return this;
    }

    public void closeDB() {
        dbsqlhelper.close();
    }

    public long insertData(String date, String v1, String v2, String hd) throws SQLException{

        ContentValues c = new ContentValues();
        c.put(COLUMN_DATE, date);
        c.put(COLUMN_HOLYDAY, hd);

        if(v1.isEmpty()){
            ContentValues cv = getVerse();
            c.put(COLUMN_VERSE_1, cv.get(COLUMN_VERSE_1).toString());
            c.put(COLUMN_VERSE_2, cv.get(COLUMN_VERSE_2).toString());
        }else {
            c.put(COLUMN_VERSE_1, v1);
            c.put(COLUMN_VERSE_2, v2);
        }

        try {
            if(dateExist(date))
                return dbsql.update(TABLE_DIARY_NAME, c, COLUMN_DATE + " = " + date, null);
            else {
                return dbsql.insert(TABLE_DIARY_NAME, null, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long insertNote(String date, int itemId, String note) throws SQLException{

        ContentValues c = new ContentValues();
        c.put(COLUMN_DATE, date);
        c.put(COLUMN_ITEM_ID, itemId);
        c.put(COLUMN_NOTE, note);

        try {
            return dbsql.insert(TABLE_NOTES_NAME, null, c);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long updateNote(String date, String itemId, String note) throws SQLException{

        ContentValues c = new ContentValues();
        c.put(COLUMN_DATE, date);
        c.put(COLUMN_ITEM_ID, itemId);
        c.put(COLUMN_NOTE, note);

        try {
            return dbsql.update(TABLE_NOTES_NAME, c, COLUMN_DATE + " = " + date
                    + " and " +
                    COLUMN_ITEM_ID + " = " + itemId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long removeNote(String date, ArrayList<String> list) throws SQLException{

        long res = 0;

        try {
            dbsql.delete(TABLE_NOTES_NAME, COLUMN_DATE + " = " + date, null);
            if (list.size() != 0){
                for(int i=0; i<list.size(); i++) {
                    ContentValues c = new ContentValues();
                    c.put(COLUMN_DATE, date);
                    c.put(COLUMN_ITEM_ID, i);
                    c.put(COLUMN_NOTE, list.get(i));
                    res = dbsql.insert(TABLE_NOTES_NAME, null, c);
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public ContentValues getNote(String date){
        Cursor c;
        ContentValues cv = new ContentValues();
        String[] col = new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_ITEM_ID, COLUMN_NOTE};
        c = dbsql.query(TABLE_NOTES_NAME, col,
                COLUMN_DATE + " = " + date, null, null, null, null);
        if (c.getCount() != 0){
            c.moveToFirst();
            for(int i=0 ; i<c.getCount() ; i++){
                cv.put(COLUMN_NOTE + i, c.getString(c.getColumnIndex(COLUMN_NOTE)));
                c.moveToNext();
            }
        }
        return cv;
    }

    private ContentValues getVerse() {
        Cursor c;
        ContentValues cv = new ContentValues();
        String[] col = new String[]{COLUMN_ID, COLUMN_VERSE_1, COLUMN_VERSE_2, COLUMN_COMMENT_3};
        c = dbsql.query(TABLE_COMMENT_NAME, col,
                COLUMN_COMMENT_3 + " = 'F' LIMIT 1", null, null, null, null);
        if(c.getCount() == 0) {
            ContentValues tcv = new ContentValues();
            tcv.put(COLUMN_COMMENT_3, "F");
            dbsql.update(TABLE_COMMENT_NAME, tcv, null, null);
            c = dbsql.query(TABLE_COMMENT_NAME, col,
                    COLUMN_COMMENT_3 + " = 'F' LIMIT 1", null, null, null, null);
        }
        c.moveToFirst();
        cv.put(COLUMN_VERSE_1, c.getString(c.getColumnIndex(COLUMN_VERSE_1)));
        cv.put(COLUMN_VERSE_2, c.getString(c.getColumnIndex(COLUMN_VERSE_2)));
        ContentValues tqmc = new ContentValues();
        tqmc.put(COLUMN_COMMENT_3, "T");
        c.moveToFirst();
        dbsql.update(TABLE_COMMENT_NAME, tqmc, COLUMN_ID + " = " + c.getString(c.getColumnIndex(COLUMN_ID)), null);
        return cv;
    }

    private boolean dateExist(String date){
        Cursor c;
        String[] col = new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_VERSE_1,
                COLUMN_VERSE_2, COLUMN_HOLYDAY};
        c = dbsql.query(TABLE_DIARY_NAME, col,
                COLUMN_DATE + " = " + date + " LIMIT 1", null, null, null, null);
        if(c.getCount() == 0)
            return false;
        return true;
    }

    public ContentValues readData(String qData) throws SQLException{

        Cursor c;
        ContentValues cv = new ContentValues();
        String[] col = new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_VERSE_1,
                COLUMN_VERSE_2, COLUMN_HOLYDAY};
        c = dbsql.query(TABLE_DIARY_NAME, col,
                COLUMN_DATE + " = " + qData, null, null, null, null);
        if(c.getCount() == 0){
            cv.put(COLUMN_DATE, qData);
            ContentValues tcv = getVerse();
            cv.put(COLUMN_VERSE_1, tcv.get(COLUMN_VERSE_1).toString());
            cv.put(COLUMN_VERSE_2, tcv.get(COLUMN_VERSE_2).toString());
            insertData(qData,tcv.get(COLUMN_VERSE_1).toString(), tcv.get(COLUMN_VERSE_1).toString(),"");
        }else {
            c.moveToFirst();
            if (c.getString(c.getColumnIndex(COLUMN_VERSE_1)).isEmpty()) {
                ContentValues tcv = getVerse();
                cv.put(COLUMN_VERSE_1, tcv.get(COLUMN_VERSE_1).toString());
                cv.put(COLUMN_VERSE_2, tcv.get(COLUMN_VERSE_2).toString());
            } else {
                cv.put(COLUMN_VERSE_1, c.getString(c.getColumnIndex(COLUMN_VERSE_1)));
                cv.put(COLUMN_VERSE_2, c.getString(c.getColumnIndex(COLUMN_VERSE_2)));
            }
            cv.put(COLUMN_DATE, c.getString(c.getColumnIndex(COLUMN_DATE)));
//            cv.put(COLUMN_NOTE, c.getString(c.getColumnIndex(COLUMN_NOTE)));
            cv.put(COLUMN_HOLYDAY, c.getString(c.getColumnIndex(COLUMN_HOLYDAY)));
        }
        return cv;
    }

    public static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_DIARY_ENTRIES);
            db.execSQL(SQL_CREATE_COMMENT_ENTRIES);
            db.execSQL(SQL_CREATE_NOTES_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_DIARY_ENTRIES);
            db.execSQL(SQL_DELETE_COMMENT_ENTRIES);
            db.execSQL(SQL_DELETE_NOTES_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void commentDBPull(){
        Cursor c;
        long q = 0;
        c = dbsql.rawQuery("SELECT count(*) FROM Comment", null);
        c.moveToFirst();
        int icount = c.getInt(0);
        if(icount == 0) {
            AccessDB ADB = new AccessDB(dbcontext);
            ADB.readFile();
            ContentValues tempc;
            tempc = new ContentValues();
            Integer i;
            for (i = 1; i <= 400/*ADB.cv1.size()*/; i++) {
                tempc.put(COLUMN_VERSE_1, ADB.cv1.getAsString(i.toString()));
                tempc.put(COLUMN_VERSE_2, ADB.cv2.getAsString(i.toString()));
                tempc.put(COLUMN_COMMENT_3, "F");
                try {
                    q = dbsql.insert(TABLE_COMMENT_NAME, null, tempc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertHolyDay(String date, String hd){
        ContentValues c = new ContentValues();
        c.put(COLUMN_DATE, date);
        c.put(COLUMN_HOLYDAY, hd);
        try {
            if(dateExist(date))
                dbsql.update(TABLE_DIARY_NAME, c, COLUMN_DATE + " = " + date, null);
            else {
                dbsql.insert(TABLE_DIARY_NAME, null, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHD(){

        int i;
        String[][] s = new String[26][];

        s[0] = new String[]{"2014321", "عید نوروز"};
        s[1] = new String[]{"2014322", "عید نوروز"};
        s[2] = new String[]{"2014323", "عید نوروز"};
        s[3] = new String[]{"2014324", "عید نوروز"};
        s[4] = new String[]{"201441", "روز جمهوری اسلامی"};
        s[5] = new String[]{"201442", "روز طبیعت-سیزده به در"};
        s[6] = new String[]{"201443", "شهادت حضرت فاطمه زهرا سلام الله علیها"};
        s[7] = new String[]{"2014513","ولادت امام علی علیه السلام"};
        s[8] = new String[]{"2014527","مبعث رسول اکرم"};
        s[9] = new String[]{"201464","رحلت حضرت امام خمینی"};
        s[10] = new String[]{"201465","قیام 15 خرداد"};
        s[11] = new String[]{"2014613","ولادت حضرت قائم عجل الله تعالی فرجه و جشن نیمه شعبان"};
        s[12] = new String[]{"2014719","شهادت حضرت علی علیه السلام"};
        s[13] = new String[]{"2014729","عید سعید فطر"};
        s[14] = new String[]{"2014730","تعطیل به مناسبت عید سعید فطر"};
        s[15] = new String[]{"2014822","شهادت امام جعفر صادق علیه السلام"};
        s[16] = new String[]{"2014105","عید سعید قربان"};
        s[17] = new String[]{"20141013","عید سعید غدیر خم"};
        s[18] = new String[]{"2014113","اسوعای حسینی"};
        s[19] = new String[]{"2014114","عاشورای حسینی"};
        s[20] = new String[]{"20141213", "اربعین حسینی"};
        s[21] = new String[]{"20141221", "رحلت رسول اکرم؛شهادت امام حسن مجتبی علیه السلام"};
        s[22] = new String[]{"20141223", "شهادت امام رضا علیه السلام"};
        s[23] = new String[]{"201519", "میلاد رسول اکرم و امام جعفر صادق علیه السلام"};
        s[24] = new String[]{"2015211", "پیروزی انقلاب اسلامی"};
        s[25] = new String[]{"2015320", "روز ملی شدن صنعت نفت ایران"};

        for(i = 0 ; i<= 25; i++){
            insertHolyDay(s[i][0], s[i][1]);
        }
    }
}
