package com.example.h_buc.activitytracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
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

public class BackgroundService extends Service {

    Records rc = new Records();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        rc.Records(getApplicationContext());

        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            public void run() {
                showNoti();
            }
        }, 5000, 5000*60);
    }

    public void showNoti(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

                rc.update(database, getApplicationContext());

                Toast.makeText(getApplicationContext(), "recorded", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
