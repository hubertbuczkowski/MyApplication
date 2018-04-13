package com.example.h_buc.activitytracker.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by h_buc on 06/04/2018.
 */

//This class makes changes in internal database
//    all changes are made using this class

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
    private static final String HISTORY_DATE = "Date";
    private static final String HISTORY_ID = "FoodId";
    private static final String HISTORY_NAME = "FoodName";
    private static final String HISTORY_WEIGHT = "Grams";
    private static final String HISTORY_PROTEIN = "Protein";
    private static final String HISTORY_CARB = "Carbs";
    private static final String HISTORY_FAT = "Fat";
    private static final String HISTORY_CALS = "Calories";
    private static final String HISTORY_TYPE = "MealType";
    private static final String HISTORY_SYNC = "Sync";

    private SQLiteDatabase db;

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
                "FoodName text not null," +
                HISTORY_ID +" text not null," +
                HISTORY_WEIGHT + " text not null," +
                HISTORY_PROTEIN + " text not null," +
                HISTORY_CARB + " text not null," +
                HISTORY_FAT + " text not null," +
                HISTORY_CALS + " text not null," +
                HISTORY_TYPE + " text not null," +
                "Sync boolean not null)");

        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
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
        db.close();
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
        db.close();
    }



    public ArrayList<Map<String, String>> readRecords(String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM Records where Date = ?";

        Cursor c = db.rawQuery(selectQuery, new String[]{date});

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



    public ArrayList<Map<String, String>> getMissingRecords()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM Records where Sync = 0";

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
                rec.put("Date", c.getString(c.getColumnIndex(RECORDS_DATE)));

                updateHR(rec.get("Date"), rec.get("Time"), rec.get("Heart Rate"), rec.get("Steps"), true);
                records.add(rec);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return records;
    }

    public ArrayList<Map<String, String>> getMissingFood()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM History where Sync = 0";

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<Map<String, String>> records = new ArrayList<>();

        if (c.moveToFirst())
        {
            do
            {
                Map<String,String> rec = new HashMap();

                rec.put("Date", c.getString(c.getColumnIndex(HISTORY_DATE)));
                rec.put("Food Id", c.getString(c.getColumnIndex(HISTORY_ID)));
                rec.put("Name", c.getString(c.getColumnIndex(HISTORY_NAME)));
                rec.put("Weight", c.getString(c.getColumnIndex(HISTORY_WEIGHT)));
                rec.put("Protein", c.getString(c.getColumnIndex(HISTORY_PROTEIN)));
                rec.put("Carb", c.getString(c.getColumnIndex(HISTORY_CARB)));
                rec.put("Fat", c.getString(c.getColumnIndex(HISTORY_FAT)));
                rec.put("Calories", c.getString(c.getColumnIndex(HISTORY_CALS)));
                rec.put("Meal", c.getString(c.getColumnIndex(HISTORY_TYPE)));

                updateMeal(rec);
                records.add(rec);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return records;
    }



    public void addMeal(String date, String id, String name, String weight,
                        String protein, String carbs, String fat, String calories, String meal, Boolean sync){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(HISTORY_DATE, date);
        values.put(HISTORY_ID, id);
        values.put(HISTORY_NAME, name);
        values.put(HISTORY_WEIGHT, weight);
        values.put(HISTORY_PROTEIN, protein);
        values.put(HISTORY_CARB, carbs);
        values.put(HISTORY_FAT, fat);
        values.put(HISTORY_CALS, calories);
        values.put(HISTORY_TYPE, meal);
        values.put(HISTORY_SYNC, sync);

        db.insert(HISTORY_TABLE, null, values);
        db.close();
    }

    public void updateMeal(Map<String, String> rec){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(HISTORY_DATE, rec.get("Date"));
        values.put(HISTORY_ID, rec.get("Food Id"));
        values.put(HISTORY_NAME, rec.get("Name"));
        values.put(HISTORY_WEIGHT, rec.get("Weight"));
        values.put(HISTORY_PROTEIN, rec.get("Protein"));
        values.put(HISTORY_CARB, rec.get("Carb"));
        values.put(HISTORY_FAT, rec.get("Fat"));
        values.put(HISTORY_CALS, rec.get("Calories"));
        values.put(HISTORY_TYPE, rec.get("Meal"));
        if(rec.get("Sync").isEmpty())
        {
            values.put(HISTORY_SYNC, true);
        }
        else
        {
            if(rec.get("Sync") == "true")
            {
                values.put(HISTORY_SYNC, true);
            }
            else
            {
                values.put(HISTORY_SYNC, false);
            }
        }

        db.update(HISTORY_TABLE, values, "FoodName=? AND MealType=? AND Date=?", new String[]{rec.get("Name"), rec.get("Meal"), rec.get("Date")});
        db.close();
    }

    public void deleteMeal(String meal, String prod, String date){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(HISTORY_TABLE, "FoodName = ? AND MealType = ? AND Date = ?", new String[]{prod, meal, date});
    }

    public ArrayList<Map<String, String>> readFood(String date){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM History where Date = ?";

        Cursor c = db.rawQuery(selectQuery, new String[]{date});

        ArrayList<Map<String, String>> records = new ArrayList<>();

        if (c.moveToFirst())
        {
            do
            {
                Map<String,String> rec = new HashMap();

                rec.put("Food Id", c.getString(c.getColumnIndex(HISTORY_ID)));
                rec.put("Name", c.getString(c.getColumnIndex(HISTORY_NAME)));
                rec.put("Weight", c.getString(c.getColumnIndex(HISTORY_WEIGHT)));
                rec.put("Protein", c.getString(c.getColumnIndex(HISTORY_PROTEIN)));
                rec.put("Carb", c.getString(c.getColumnIndex(HISTORY_CARB)));
                rec.put("Fat", c.getString(c.getColumnIndex(HISTORY_FAT)));
                rec.put("Calories", c.getString(c.getColumnIndex(HISTORY_CALS)));
                rec.put("Meal", c.getString(c.getColumnIndex(HISTORY_TYPE)));

                records.add(rec);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return records;
    }
}
