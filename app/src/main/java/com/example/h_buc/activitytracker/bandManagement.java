package com.example.h_buc.activitytracker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Driver;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.example.h_buc.activitytracker.Helpers.CustomBluetoothProfile;
import com.example.h_buc.activitytracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by h_buc on 13/11/2017.
 */

public class bandManagement extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    FirebaseDatabase db;
    DatabaseReference dRef ;
    ImageButton usrBtn, logout;
    TextView bodyCal, exerciseCal, consumedCal, totalCal;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    LinearLayout ln;

    CircleProgressView mCircleView;
    FloatingActionButton addBtn;

    int[] height = new int[1];
    float[] weight = new float[1];

    String stepAmountFromGoogle = "0";

    Records rc;

    private BottomSheetBehavior mBottomSheetBehavior;
    BottomSheetDialogFragment bottomSheetDialogFragment;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        //initialie();
        //getBoundedDevice();

        bottomSheetDialogFragment = new FoodDiaryFragment();





        currentUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());


        database.child("Height").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                height[0] = (int) Integer.parseInt(dataSnapshot.getValue().toString());
                updateCals();
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        database.child("Weight").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                weight[0] = (float) Float.parseFloat(dataSnapshot.getValue().toString());
                updateCals();
            }
            public void onCancelled(DatabaseError databaseError) {}
        });


        bodyCal = findViewById(R.id.bodyCals);
        exerciseCal = findViewById(R.id.exerciseCals);
        consumedCal = findViewById(R.id.Consumed);
        totalCal = findViewById(R.id.leftCals);
        mCircleView = findViewById(R.id.circleView);
        mCircleView.setTextMode(TextMode.TEXT);

        long total = 0;

        // Create a GoogleApiClient instance
        GoogleApiClient mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        mClient.connect();


        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(getEndOfDay(now));
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        long startTime = cal.getTimeInMillis();

        final DataSource ds = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();

        DataReadRequest req = new DataReadRequest.Builder()
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



        usrBtn = (ImageButton) findViewById(R.id.userSettings);
        logout = (ImageButton) findViewById(R.id.logout);
        addBtn = findViewById(R.id.addButton);

        addBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //addFood();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        usrBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                userPref();
            }
        });
        logout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Logout();


            }
        });

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityDetail();
            }
        });

        ln = findViewById(R.id.linearLayout);

        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail();
            }
        });

        Intent i = new Intent(this, BackgroundService.class);

        getApplicationContext().startService(i);
    }

    void activityDetail(){
        final Dialog dialog = new Dialog(bandManagement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_activity_details);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

        DatabaseReference i = database.child("Records").child("06032018");
        final ValueLineChart mSteps = (ValueLineChart) dialog.findViewById(R.id.steps_timeline);
        final ValueLineChart mHeart = (ValueLineChart) dialog.findViewById(R.id.heart_reate_timeline);

        final ValueLineSeries steps = new ValueLineSeries();
        final ValueLineSeries heart = new ValueLineSeries();
        steps.setColor(0xFF56B7F1);
        heart.setColor(0xFF0DFFC1);

        i.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Map<String,Object> res = (Map<String, Object>) dataSnapshot.getValue();
                SortedSet<String> keys = new TreeSet<>(res.keySet());
                for (String key : keys) {
                    Map<String, String> entry = (Map) res.get(key);
                    steps.addPoint(new ValueLinePoint(key, Integer.parseInt(entry.get("Steps"))));
                    if(entry.get("Heart Rate") != null) {
                        heart.addPoint(new ValueLinePoint(key, Integer.parseInt(entry.get("Heart Rate"))));
                    }
                    else
                    {
                        heart.addPoint(new ValueLinePoint(key, 0));
                    }
                }

                mSteps.addSeries(steps);
                mSteps.startAnimation();
                mHeart.addSeries(heart);
                mHeart.startAnimation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dialog.show();
    }

    void foodDetail(){
        final Dialog dialog = new Dialog(bandManagement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_food_details);
        dialog.show();
    }



    void sendData(String results){
        dRef = db.getReference("SimpleMessage");
        dRef.setValue(results);
    }


    void userPref()
    {
        Intent intent = new Intent(bandManagement.this, userPref.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void Logout(){
        SaveSharedPreference.clear(getApplicationContext());
        Intent intent = new Intent(bandManagement.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void addFood(){
        Intent intent = new Intent(bandManagement.this, searchFood.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
    private void showDataSet(DataSet dataSet) {
        if(!dataSet.isEmpty()) {
            Value f = dataSet.getDataPoints().get(0).getValue(dataSet.getDataPoints().get(0).getDataType().getFields().get(0));
            stepAmountFromGoogle = f.toString();
        }
        else
        {
            stepAmountFromGoogle = "0";
        }
        updateCals();

    }

    private void updateCals(){

        final int[] bodycals = new int[1];
        int excals;
        int conscals;
        int totcals;

        String stepsStr = stepAmountFromGoogle;



        bodycals[0] = (int) (10*weight[0] + 6.25*height[0] - 5*22 + 5);
        excals = (55 * Integer.parseInt(stepsStr)) / 1250;
        conscals = 1681;
        totcals = bodycals[0] + excals - conscals;

        mCircleView.setMaxValue(bodycals[0] + excals);
        mCircleView.setValue(1681);

        mCircleView.setText("1681/" + Integer.toString(bodycals[0] + excals));

        bodyCal.setText(Integer.toString(bodycals[0]));


        exerciseCal.setText(Integer.toString(excals));
        consumedCal.setText(Integer.toString(conscals));
        totalCal.setText(Integer.toString(totcals));

    }

    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        //LocalDateTime startOfDay = localDateTime.atStartOfDay();
        return localDateTimeToDate(startOfDay);
    }

    private static Date localDateTimeToDate(LocalDateTime startOfDay) {
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
}
