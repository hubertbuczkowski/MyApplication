package com.example.h_buc.activitytracker;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import java.lang.reflect.Array;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class DatabaseLocal extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Activity.db";

    //User_Pass TABLE
    private static final String UPASS_TABLE = "User_Pass";
    private static final String UPASS_ID = "ID";
    private static final String UPASS_NAME = "Username";
    private static final String UPASS_PASS = "Password";

    SQLiteDatabase db;

    public DatabaseLocal(Context cont)
    {
        super(cont, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table Products (" +
                "ID integer primary key autoincrement, " +
                "Username text not null, " +
                "Password text not null)");

        this.db = db;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + UPASS_TABLE);

        onCreate(db);
    }

    public void startFunction()
    {
        insertUser("Hubert", "Hubert");
    }

    /*****   QUERIES     *****/

    public boolean checkIfExist(String uname, String passwd)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + UPASS_TABLE + "WHERE Username = " + uname;

        Cursor c = db1.rawQuery(selectQuery, null);


        if (c.moveToFirst())
        {
            c.close();
            db1.close();
            return true;
        }
        else
        {
            c.close();
            db1.close();
            return false;
        }
    }

    public String insertUser(String uname, String passwd)
    {
        if(checkIfExist(uname,passwd))
        {
            SQLiteDatabase db2 = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(UPASS_NAME, uname);
            values.put(UPASS_PASS, passwd);

            db2.insert(UPASS_TABLE, null, values);

            db2.close();
            return "User Created";
        }
        else
        {
            return "User Exist";
        }
    }
}
