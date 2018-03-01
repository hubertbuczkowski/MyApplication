package com.example.h_buc.activitytracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by h_buc on 27/02/2018.
 */

public class BackgroundService extends IntentService {

    Records rc = new Records();

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getDataString();

        rc.Records(getApplicationContext());

        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {


            @Override
            public void run() {
                showNoti();
            }
        }, 5000, 1000*60);
    }

    public void showNoti(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

                rc.update(database);

                Toast.makeText(getApplicationContext(), "recorded", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
