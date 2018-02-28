package com.example.h_buc.activitytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by h_buc on 22/01/2018.
 */

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "username";
    static final String PREF_USER_PASS= "password";

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