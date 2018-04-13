package com.example.h_buc.activitytracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Settings.Global.getString;
import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS;
import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;

/**
 * Created by h_buc on 13/04/2018.
 */

public class NotificationManagerInternal {

    public static void showNotification(Context ctx, String title, int i){


        String description;
        Intent intent = new Intent( ctx, searchFood.class);

        if(title.equals("Breakfast"))
        {
            description = "Good morning, it is high time to eat something!";
        }
        else if(title.equals("Weight"))
        {
            description = "Good morning, check your weight!";
            intent = new Intent(ctx, bandManagement.class);
        }
        else
        {
            description = "Hey, it is high time to eat something!";
        }

        intent.putExtra("Meal Type", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity( ctx, 0, intent, 0);

        int notifyID = i;
        String CHANNEL_ID = "my_channel_0"+i;// The id of the channel.
        CharSequence name = "ChannelSample";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID);

        b.setAutoCancel(true)
                .setDefaults(DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker("Lifestyle Tracker")
                .setContentTitle(title)
                .setContentText(description)
                .setDefaults(DEFAULT_LIGHTS| DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(notifyID, b.build());
    }

}
