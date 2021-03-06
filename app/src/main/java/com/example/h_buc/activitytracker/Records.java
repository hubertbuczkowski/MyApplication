package com.example.h_buc.activitytracker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.h_buc.activitytracker.Helpers.CheckConnection;
import com.example.h_buc.activitytracker.Helpers.internalDatabaseManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.firebase.database.DatabaseReference;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by h_buc on 20/11/2017.
 */

//This class is responsible for mannaging all heart rate, steps and storing them on both databases

public class Records implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    volatile private String steps;
    volatile private String heartRate;
    volatile GoogleApiClient mClient;
    volatile DataSource ds;
    volatile DataReadRequest req;
//    volatile bandController bd;

    public void Records(Context ctx){

        this.steps = "0";
        this.heartRate = "65";
//        this.bd = new bandController(ctx);

        setClients(ctx);
        update();
    }

    //define clients for Google Fit API
    private void setClients(Context ctx){
        mClient = new GoogleApiClient.Builder(ctx)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .build();

        mClient.connect();

        ds = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();
    }

    //Send back number of steps
    public String getSteps()
    {
        return this.steps;
    }

    //Send back heart rate
    public String getHeart()
    {
        return this.heartRate;
    }

    //get end of the day time for Google fit
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    //Stores number of steps in variable
    private void showDataSet(DataSet dataSet) {
        if(!dataSet.isEmpty()) {
            Value f = dataSet.getDataPoints().get(0).getValue(dataSet.getDataPoints().get(0).getDataType().getFields().get(0));
            this.steps = f.toString();
        }
        else
        {
            this.steps = "0";
        }
    }

    //upoad all data into internal and external database
    private void uploadData(DataSet dataSet, final DatabaseReference database, final Context ctx){
        showDataSet(dataSet);

        final Date date = new Date();
        final String current_date = new SimpleDateFormat("ddMMyyyy").format(date);
        final String current_time = new SimpleDateFormat("HH:mm").format(date);
        final internalDatabaseManager db = new internalDatabaseManager(ctx);

        new Thread(new Runnable() {
            public void run() {
                if(CheckConnection.InternetConnection())
                {
                    database.child("Records").child(current_date).child(current_time).child("Steps").setValue(steps);
                    db.addRecord(current_date, current_time, "0", steps, true);
                }
                else
                {
                    db.addRecord(current_date, current_time, "0", steps, false);
                }

//                bandController bd = new bandController(ctx);
                String hr = "-2";
                try {
                    bandController bd = new bandController();
                    bd.getBoundedDevice(ctx);
                    hr = bd.startScanHeartRate(ctx);
                }catch (Exception e)
                {
                    hr = "-1";
                }
                if(CheckConnection.InternetConnection())
                {
                    database.child("Records").child(current_date).child(current_time).child("Heart Rate").setValue(hr);
                    db.updateHR(current_date, current_time, hr, steps, true);
                }
                else
                {
                    db.updateHR(current_date, current_time, hr, steps, false);
                }

//                bd = null;
            }
        }).start();
    }

    //get Step data from google fit and invoke showDataSet
    public void update(){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(getEndOfDay(now));
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        long startTime = cal.getTimeInMillis();

        req = new DataReadRequest.Builder()
                .aggregate(ds,DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> resultsData = Fitness.HistoryApi.readData(mClient, req);

        resultsData.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(@NonNull DataReadResult dataReadResult) {
                showDataSet(dataReadResult.getBuckets().get(0).getDataSets().get(0));
            }
        });
    }

    //same as previous function but used in other place
    public void update(final DatabaseReference database, final Context ctx){
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(getEndOfDay(now));
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        long startTime = cal.getTimeInMillis();


        req = new DataReadRequest.Builder()
                .aggregate(ds,DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> resultsData = Fitness.HistoryApi.readData(mClient, req);

        resultsData.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(@NonNull DataReadResult dataReadResult) {
                uploadData(dataReadResult.getBuckets().get(0).getDataSets().get(0), database, ctx);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private static Date localDateTimeToDate(LocalDateTime startOfDay) {
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
}
