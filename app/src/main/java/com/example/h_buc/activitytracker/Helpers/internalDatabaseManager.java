package com.example.h_buc.activitytracker.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h_buc on 06/04/2018.
 */

public class internalDatabaseManager  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Lifestyle.Tracker.db";

    //Records TABLE
    private static final String RECORDS_TABLE = "Records";
    private static final String RECORDS_DATE = "Date";
    private static final String RECORDS_TIME = "Time";
    private static final String RECORDS_HR = "HeartRate";
    private static final String RECORDS_STEPS = "Steps";
    private static final String RECORDS_SYNC = "Sync";

    //History TABLE
    private static final String HISTORY_TABLE = "History";
    private static final String HISTORY_TIME = "Date";
    private static final String HISTORY_ID = "FoodId";
    private static final String HISTORY_NAME = "FoodName";
    private static final String HISTORY_WEIGHT = "Grams";
    private static final String HISTORY_PROTEIN = "Protein";
    private static final String HISTORY_CARB = "Carbs";
    private static final String HISTORY_FAT = "Fat";
    private static final String HISTORY_CALS = "Calories";
    private static final String HISTORY_TYPE = "MealType";
    private static final String HISTORY_SYNC = "Sync";

    SQLiteDatabase db;

    public internalDatabaseManager (Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Records (" +
                "Date text not null," +
                "Time text not null," +
                "HeartRate integer not null," +
                "Steps integer not null," +
                "sync boolean not null)");

        db.execSQL("create table History (" +
                "Date text not null," +
                "Time text not null," +
                "FoodName text not null," +
                "Grams integer not null," +
                HISTORY_PROTEIN + " integer not null," +
                HISTORY_CARB + " integer not null," +
                HISTORY_FAT + " integer not null," +
                HISTORY_CALS + " integer not null," +
                HISTORY_TYPE + " integer not null," +
                "Sync boolean not null)");

        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + RECORDS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);

        onCreate(db);
    }

    public void addRecord(String date, String time, String hr, String steps, Boolean sync){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(RECORDS_TIME, time);
        values.put(RECORDS_DATE, date);
        values.put(RECORDS_HR, hr);
        values.put(RECORDS_STEPS, steps);
        values.put(RECORDS_SYNC, sync);

        db.insert(RECORDS_TABLE, null, values);
        this.db.close();
    }

    public void updateHR(String date, String time, String hr, String steps, Boolean sync){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(RECORDS_DATE, date);
        values.put(RECORDS_TIME, time);
        values.put(RECORDS_HR, hr);
        values.put(RECORDS_STEPS, steps);
        values.put(RECORDS_SYNC, sync);

        db.update(RECORDS_TABLE, values, "Time=? AND Date=?", new String[]{time, date});
        this.db.close();
    }

    public ArrayList<Map<String, String>> readTodayRecords()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String selectQuery = "SELECT * FROM Records where Date > " + currentDate + " 00:00:00 and Date < " + currentDate + " 23:59:59";

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Map<String, String>> records = new ArrayList<>();

        if (c.moveToFirst())
        {
            do
            {
                Map<String,String> rec = new HashMap();

                rec.put("Heart Rate", c.getString(c.getColumnIndex(RECORDS_HR)));
                rec.put("Steps", c.getString(c.getColumnIndex(RECORDS_STEPS)));
                rec.put("Time", c.getString(c.getColumnIndex(RECORDS_TIME)));

                records.add(rec);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return records;
    }
}
