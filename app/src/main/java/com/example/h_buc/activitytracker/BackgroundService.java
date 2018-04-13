package com.example.h_buc.activitytracker;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.h_buc.activitytracker.Helpers.CheckConnection;
import com.example.h_buc.activitytracker.Helpers.FirebaseManagement;
import com.example.h_buc.activitytracker.Helpers.internalDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by h_buc on 27/02/2018.
 */

public class BackgroundService extends Service {

    Records rc = new Records();
    Date currentDate = new Date();
    internalDatabaseManager db;

    Date lastTime = new Date();

    Boolean breakfast = false;
    Boolean lunch = false;
    Boolean dinner = false;
    Boolean supper = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        rc.Records(getApplicationContext());
        db = new internalDatabaseManager(getApplicationContext());
        synchronise();
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            public void run() {
                startSaving();
            }
        }, 5000, 5000*60);
    }

    public void startSaving(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

                if(currentDate != new Date())
                {
                    currentDate = new Date();
                    breakfast = false;
                    lunch = false;
                    dinner = false;
                    supper = false;
                    synchronise();
                }

                rc.update(database, getApplicationContext());

                checkNoti();
            }
        });
    }

    private void synchronise(){
        new Thread(new Runnable() {
            public void run() {
                if(CheckConnection.InternetConnection())
                {
                    ArrayList<Map<String, String>> records = db.getMissingRecords();
                    ArrayList<Map<String, String>> food = db.getMissingFood();

                    for(Map<String, String> entry : records)
                    {
                        FirebaseManagement.addRecord(entry.get("Date"), entry.get("Time"), entry.get("Heart Rate"), entry.get("Steps"));
                    }

                    for(Map<String, String> entry : food)
                    {
                        FirebaseManagement.addMissingFood(entry.get("Date"), entry.get("Name"), entry.get("Food Id"),entry.get("Weight"),entry.get("Protein"),
                                entry.get("Carb"),entry.get("Fat"),entry.get("Calories"),entry.get("Meal"));
                    }
                }
            }
        }).start();
    }

    private void checkNoti(){
        if(!breakfast)
        {
            String step =  rc.getSteps();
            if(Integer.parseInt(step) > 0)
            {
                NotificationManagerInternal.showNotification(getApplicationContext(), "Breakfast", 1);
                NotificationManagerInternal.showNotification(getApplicationContext(), "Weight", 2);
                lastTime = new Date();
                breakfast = true;
            }
        }
        else
        {
            long diff = lastTime.getTime() - new Date().getTime();
            diff = diff * 1000 * 60;
            if(!lunch)
            {
                if(diff > 150)
                {
                    NotificationManagerInternal.showNotification(getApplicationContext(), "Lunch", 3);
                    lastTime = new Date();
                    lunch = true;
                }
            }
            else if(!dinner)
            {
                if(diff > 150)
                {
                    NotificationManagerInternal.showNotification(getApplicationContext(), "Dinner", 4);
                    lastTime = new Date();
                    dinner = true;
                }
            }
            else if(!supper)
            {
                if(diff > 150)
                {
                    NotificationManagerInternal.showNotification(getApplicationContext(), "Supper", 5);
                    lastTime = new Date();
                    supper = true;
                }
            }
        }
    }

}
