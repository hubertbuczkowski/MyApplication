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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.w3c.dom.Text;

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
    LinearLayout ln1, ln2, ln3, ln4, ln5;

    TextView bPro, bCal, bFat, bCarb,
            lPro, lCal, lFat, lCarb,
            dPro, dCal, dFat, dCarb,
            sPro, sCal, sFat, sCarb,
            snPro, snCal, snFat, snCarb,
            tPro, tCal, tFat, tCarb;

    CircleProgressView mCircleView;
    FloatingActionButton addBtn;

    private BottomSheetBehavior mBottomSheetBehavior;
    BottomSheetDialogFragment bottomSheetDialogFragment;

    int bodyCals;
    int excals;
    int conscals;
    int totcals;
    Map<String, Object> foodDetails;
    Map<String, Map<String, String>> activityRecords = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initialize();

        addBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
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


        ln1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail("Breakfast");
            }
        });
        ln2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail("Lunch");
            }
        });
        ln3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail("Dinner");
            }
        });
        ln4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail("Supper");
            }
        });
        ln5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodDetail("Snack");
            }
        });

        Intent i = new Intent(this, BackgroundService.class);

        getApplicationContext().startService(i);
    }

    protected void onResume(){
        super.onResume();
        Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();
        resetData();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
        readExercisesFirebase(database);
    }

    void resetData(){
        bPro.setText("0");
        bCal.setText("0");
        bFat.setText("0");
        bCarb.setText("0");
        lPro.setText("0");
        lCal.setText("0");
        lFat.setText("0");
        lCarb.setText("0");
        dPro.setText("0");
        dCal.setText("0");
        dFat.setText("0");
        dCarb.setText("0");
        sPro.setText("0");
        sCal.setText("0");
        sFat.setText("0");
        sCarb.setText("0");
        snPro.setText("0");
        snCal.setText("0");
        snFat.setText("0");
        snCarb.setText("0");
        tPro.setText("0");
        tCal.setText("0");
        tFat.setText("0");
        tCarb.setText("0");
    }

    void initialize(){
        bottomSheetDialogFragment = new FoodDiaryFragment();
        currentUser = mAuth.getCurrentUser();
        bodyCal = findViewById(R.id.bodyCals);
        exerciseCal = findViewById(R.id.exerciseCals);
        consumedCal = findViewById(R.id.Consumed);
        totalCal = findViewById(R.id.leftCals);
        mCircleView = findViewById(R.id.circleView);
        mCircleView.setTextMode(TextMode.TEXT);
        usrBtn = (ImageButton) findViewById(R.id.userSettings);
        logout = (ImageButton) findViewById(R.id.logout);
        addBtn = findViewById(R.id.addButton);
        ln1 = findViewById(R.id.linearBreakfast);
        ln2 = findViewById(R.id.linearLunch);
        ln3 = findViewById(R.id.linearDinner);
        ln4 = findViewById(R.id.linearSupper);
        ln5 = findViewById(R.id.linearSnack);

        //Food details
        bPro = findViewById(R.id.breakProt);
        bCal = findViewById(R.id.breakCal);
        bFat = findViewById(R.id.breakFat);
        bCarb = findViewById(R.id.breakCarb);
        lPro = findViewById(R.id.lunchProt);
        lCal = findViewById(R.id.lunchCal);
        lFat = findViewById(R.id.lunchFat);
        lCarb = findViewById(R.id.lunchCarb);
        dPro = findViewById(R.id.dinnerProt);
        dCal = findViewById(R.id.dinnerCal);
        dFat = findViewById(R.id.dinnerFat);
        dCarb = findViewById(R.id.dinnerCarb);
        sPro= findViewById(R.id.supperProt);
        sCal = findViewById(R.id.supperCal);
        sFat = findViewById(R.id.supperFat);
        sCarb = findViewById(R.id.supperCarb);
        snPro = findViewById(R.id.snackProt);
        snCal = findViewById(R.id.snackCal);
        snFat = findViewById(R.id.snackFat);
        snCarb = findViewById(R.id.snackCarb);
        tPro = findViewById(R.id.totalProt);
        tCal = findViewById(R.id.totalCal);
        tFat = findViewById(R.id.totalFat);
        tCarb = findViewById(R.id.totalCarb);
    }

    double calculateHR(long time, int hr)
    {
        int age = Integer.parseInt(SaveSharedPreference.getPrefAge(getApplicationContext()));
        float weight = Float.parseFloat(SaveSharedPreference.getPrefWeight(getApplicationContext()));
        if(SaveSharedPreference.getPrefGender(getApplicationContext()) == "Male")
        {
            return (((age * 0.0217) - (weight * 0.1988) + (hr * 0.6309) - 55.0969) * time)/4.184;
        }
        else
        {
            return (((age * 0.074) - (weight * 0.1263) + (hr * 0.4472) - 20.4022) * time)/4.184;
        }
    }

    double calculateSteps(int steps){
        return steps  * 0.044;
    }

    void calculateFood(Map<String, Object> prods, String key)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        for(String product : prods.keySet())
        {
            Map<String, String> details = (Map) prods.get(product);
            if(key.equals("Breakfast"))
            {
                float cummulator = 0;
                cummulator = Float.parseFloat(bPro.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
                bPro.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(bCal.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
                bCal.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(bFat.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
                bFat.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(bCarb.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
                bCarb.setText(df.format(cummulator));
            }
            if(key.equals("Lunch"))
            {
                float cummulator = 0;
                cummulator = Float.parseFloat(lPro.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
                lPro.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(lCal.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
                lCal.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(lFat.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
                lFat.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(lCarb.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
                lCarb.setText(df.format(cummulator));
            }
            if(key.equals("Dinner"))
            {
                float cummulator = 0;
                cummulator = Float.parseFloat(dPro.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
                dPro.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(dCal.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
                dCal.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(dFat.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
                dFat.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(dCarb.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
                dCarb.setText(df.format(cummulator));

            }
            if(key.equals("Supper"))
            {
                float cummulator = 0;
                cummulator = Float.parseFloat(sPro.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
                sPro.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(sCal.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
                sCal.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(sFat.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
                sFat.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(sCarb.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
                sCarb.setText(df.format(cummulator));
            }
            if(key.equals("Snack"))
            {
                float cummulator = 0;
                cummulator = Float.parseFloat(snPro.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
                snPro.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(snCal.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
                snCal.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(snFat.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
                snFat.setText(df.format(cummulator));
                cummulator = 0;
                cummulator = Float.parseFloat(snCarb.getText().toString());
                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
                snCarb.setText(df.format(cummulator));
            }

        }
    }

    void sumFood(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        float prot = 0;
        float carb = 0;
        float fat = 0;
        float cal = 0;

        prot = prot + Float.parseFloat(bPro.getText().toString());
        prot = prot + Float.parseFloat(lPro.getText().toString());
        prot = prot + Float.parseFloat(dPro.getText().toString());
        prot = prot + Float.parseFloat(sPro.getText().toString());
        prot = prot + Float.parseFloat(snPro.getText().toString());
        tPro.setText(df.format(prot));
        carb = carb + Float.parseFloat(bCarb.getText().toString());
        carb = carb + Float.parseFloat(lCarb.getText().toString());
        carb = carb + Float.parseFloat(dCarb.getText().toString());
        carb = carb + Float.parseFloat(sCarb.getText().toString());
        carb = carb + Float.parseFloat(snCarb.getText().toString());
        tCarb.setText(df.format(carb));
        fat = fat + Float.parseFloat(bFat.getText().toString());
        fat = fat + Float.parseFloat(lFat.getText().toString());
        fat = fat + Float.parseFloat(dFat.getText().toString());
        fat = fat + Float.parseFloat(sFat.getText().toString());
        fat = fat + Float.parseFloat(snFat.getText().toString());
        tFat.setText(df.format(fat));
        cal = cal + Float.parseFloat(bCal.getText().toString());
        cal = cal + Float.parseFloat(lCal.getText().toString());
        cal = cal + Float.parseFloat(dCal.getText().toString());
        cal = cal + Float.parseFloat(sCal.getText().toString());
        cal = cal + Float.parseFloat(snCal.getText().toString());
        tCal.setText(String.valueOf((int) cal));
    }

    void updateFood(Map<String, Object> entry)
    {
        foodDetails = entry;
        for(String key : entry.keySet())
        {
            calculateFood((Map) entry.get(key), key);
        }
        sumFood();
    }

    void readExercisesFirebase (DatabaseReference database)
    {
        database.child("Records").child(new SimpleDateFormat("ddMMyyyy").format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> res = (Map<String, Object>) dataSnapshot.getValue();
                if(dataSnapshot.exists()) {
                    SortedSet<String> keys = new TreeSet<>(res.keySet());
                    int previousSteps = 0;
                    String previousTime = "00:00";
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date date1 = new Date();
                    Date date2 = new Date();
                    int steps = 0;
                    int hr = 0;
                    double doubleExcals = 0;
                    activityRecords.clear();

                    for (String key : keys) {
                        if(!key.equals("Food"))
                        {
                            Map<String, String> entry = (Map) res.get(key);
                            activityRecords.put(key, entry);

                            try {
                                date1 = format.parse(previousTime);
                                date2 = format.parse(key);
                                previousTime = key;
                            } catch (Exception e) {}

                            long diff = date2.getTime() - date1.getTime();
                            diff = diff / 60000;

                            steps = Integer.parseInt(entry.get("Steps"));
                            if (entry.get("Heart Rate") != null) {
                                hr = Integer.parseInt(entry.get("Heart Rate"));
                            } else {
                                hr = 0;
                            }

                            if (hr > 80 && steps > previousSteps) {
                                doubleExcals = (doubleExcals + ((calculateHR(diff, hr) + calculateSteps(steps - previousSteps)) / 2));
                                previousSteps = steps;
                            } else {
                                if (hr > 80 || steps > previousSteps) {
                                    if (hr > 80) {
                                        doubleExcals = (doubleExcals + calculateHR(diff, hr));
                                    } else {
                                        doubleExcals = (doubleExcals + calculateSteps(steps - previousSteps));
                                        previousSteps = steps;
                                    }
                                }
                            }
                        }
                        else
                        {
                            updateFood((Map) res.get(key));
                        }
                    }
                    excals = (int) doubleExcals;
                    updateCals();
                } else
                {
                    excals = 0;
                    updateCals();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        SortedSet<String> keys = new TreeSet<>(activityRecords.keySet());
        for (String key : keys) {
            Map<String, String> entry = (Map) activityRecords.get(key);
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



        dialog.show();
    }

    void foodDetail(String meal){
        final Dialog dialog = new Dialog(bandManagement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_food_details);
        LinearLayout ln = dialog.findViewById(R.id.linearLayout2);

        dialog.show();
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


    private void updateCals(){

        int bodycals = 0;
        int conscals;
        int totcals;

        String testWeight = SaveSharedPreference.getPrefWeight(getApplicationContext());
        String testHeight = SaveSharedPreference.getPrefHeight(getApplicationContext());

        if(testHeight.isEmpty() || testWeight.isEmpty())
        {
            userPref();
            Toast.makeText(getApplicationContext(), "Need to fill user details", Toast.LENGTH_SHORT).show();
        }
        else {
            float weight = Float.parseFloat(SaveSharedPreference.getPrefWeight(getApplicationContext()));
            float height = Float.parseFloat(SaveSharedPreference.getPrefHeight(getApplicationContext()));


            bodycals = (int) (10 * weight + 6.25 * height - 5 * 22 + 5);
            conscals = Integer.parseInt(tCal.getText().toString());
            totcals = bodycals + this.excals - conscals;

            mCircleView.setMaxValue(bodycals + this.excals);
            mCircleView.setValue(conscals);

            mCircleView.setText(conscals + "/" + Integer.toString(bodycals + this.excals));

            bodyCal.setText(Integer.toString(bodycals));


            exerciseCal.setText(Integer.toString(this.excals));
            consumedCal.setText(Integer.toString(conscals));
            totalCal.setText(Integer.toString(totcals));
        }
    }

}
