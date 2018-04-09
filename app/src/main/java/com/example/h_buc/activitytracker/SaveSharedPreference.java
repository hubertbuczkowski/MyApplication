package com.example.h_buc.activitytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by h_buc on 22/01/2018.
 */

public class SaveSharedPreference
{
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_PASS = "password";
    static final String PREF_GENDER = "gender";
    static final String PREF_AGE = "age";
    static final String PREF_FIRST_NAME = "first name";
    static final String PREF_SURNAME = "surnmane";
    static final String PREF_HEIGHT = "height";
    static final String PREF_WEIGHT = "weight";
    static final String PREF_GOAL = "goal";
    static final String PREF_OPERATING_DATA = "goal";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_USER_PASS, password);
        editor.commit();
    }

    public static void setDetails(Context ctx, String gender, String age, String fname, String sname, String height, String weight, int goal){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_GENDER, gender);
        editor.putString(PREF_AGE, age);
        editor.putString(PREF_FIRST_NAME, fname);
        editor.putString(PREF_SURNAME, sname);
        editor.putString(PREF_HEIGHT, height);
        editor.putString(PREF_WEIGHT, weight);
        editor.putInt(PREF_GOAL, goal);
        editor.commit();
    }

    public static void setPrefWeight(Context ctx, String weight){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_WEIGHT, weight);
        editor.commit();
    }

    public static String getPrefWeight(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_WEIGHT, "");
    }

    public static String getPrefHeight(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_HEIGHT, "");
    }

    public static String getPrefAge(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_AGE, "");
    }

    public static String getPrefGender(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_GENDER, "");
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_PASS, "");
    }

    public static void clear(Context ctx)
    {
        getSharedPreferences(ctx).edit().clear().commit();
    }
}