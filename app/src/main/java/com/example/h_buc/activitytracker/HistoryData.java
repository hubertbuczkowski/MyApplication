package com.example.h_buc.activitytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h_buc.activitytracker.Helpers.CheckConnection;
import com.example.h_buc.activitytracker.Helpers.FirebaseManagement;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by h_buc on 11/04/2018.
 */

public class HistoryData extends AppCompatActivity
{
    ImageButton  hisBack;
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
    String workingDate;

    int excals;
    Map<String, Object> foodDetails;
    Map<String, Map<String, String>> activityRecords = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);
        initialize();

        workingDate = getIntent().getExtras().getString("Date");

        TextView txt = findViewById(R.id.historyDate);

        txt.setText(workingDate.substring(0, 2) +'.'+ workingDate.substring(2, 4) +'.'+ workingDate.substring(4, 8));


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
        hisBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        Intent i = new Intent(this, BackgroundService.class);

        getApplicationContext().startService(i);
    }

    protected void onResume(){
        super.onResume();
        Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();
        resetData();
        readExercisesFirebase();
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
        currentUser = mAuth.getCurrentUser();
        bodyCal = findViewById(R.id.bodyCals);
        exerciseCal = findViewById(R.id.exerciseCals);
        consumedCal = findViewById(R.id.Consumed);
        totalCal = findViewById(R.id.leftCals);
        mCircleView = findViewById(R.id.circleView);
        mCircleView.setTextMode(TextMode.TEXT);
        hisBack = findViewById(R.id.historyBack);
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

    void readExercisesFirebase ()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
        database.child("Records").child(workingDate).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        if(!key.equals("Food") && !key.equals("Weight"))
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

                            if(diff > 10)
                            {
                                diff = 10;
                            }

                            if (entry.get("Steps") != null) {
                                steps = Integer.parseInt(entry.get("Steps"));
                            } else {
                                steps = previousSteps;
                            }
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
                            if(!key.equals("Weight")) {
                                updateFood((Map) res.get(key));
                            }
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

    private void updateCals(){

        int bodycals = 0;
        int conscals;
        int totcals;

        String testWeight = SaveSharedPreference.getPrefWeight(getApplicationContext());
        String testHeight = SaveSharedPreference.getPrefHeight(getApplicationContext());

        if(testHeight.isEmpty() || testWeight.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Need to fill user details", Toast.LENGTH_SHORT).show();
        }
        else {
            float weight = Float.parseFloat(SaveSharedPreference.getPrefWeight(getApplicationContext()));
            float height = Float.parseFloat(SaveSharedPreference.getPrefHeight(getApplicationContext()));
            int age = Integer.parseInt(SaveSharedPreference.getPrefAge(getApplicationContext()));


            if(SaveSharedPreference.getPrefGender(getApplicationContext()).equals("Male"))
            {
                bodycals = (int) (10 * weight + 6.25 * height - 5 * age + 5);
            }
            else
            {
                bodycals = (int) (10 * weight + 6.25 * height - 5 * age - 161);
            }
            switch (SaveSharedPreference.getPrefGoal(getApplicationContext())){
                case 0:
                    bodycals = bodycals - 300;
                    break;
                case 2:
                    bodycals = bodycals + 300;
                    break;
            }
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

    void foodDetail(final String meal){
        final Dialog dialog = new Dialog(HistoryData.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_food_details);
        final ListView ln = dialog.findViewById(R.id.foodListView);
        Button add = dialog.findViewById(R.id.foodDialogAddButton);
        add.setVisibility(View.INVISIBLE);
        TextView tx = dialog.findViewById(R.id.foodDialogType);
        tx.setText(meal);

        final ArrayList<foodLinear> food = new ArrayList<foodLinear>();
        foodLinearAdapter adapter;

        Map<String, Object> prods = null;
        if(foodDetails != null)
        {
            prods = (Map) foodDetails.get(meal);
        }
        if(prods != null)
        {
            for(String key : prods.keySet())
            {
                Map<String, String> product = (Map) prods.get(key);
                foodLinear fl = new foodLinear(product.get("Name"),
                        product.get("Weight"), product.get("Protein"), product.get("Carb"), product.get("Fat"), product.get("Calories"));
                food.add(fl);

            }
            adapter = new foodLinearAdapter(this, food);
            ln.setAdapter(adapter);
        }

//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addFood(meal);
//            }
//        });

        ln.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
                builder.setTitle("What to do");
                builder.setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int which) {
                        if (which == 1) {
                            final String foodname = food.get(i).foodName;
                            new Thread(new Runnable() {
                                public void run() {
                                    if(CheckConnection.InternetConnection()) {
                                        FirebaseManagement.deleteFood(meal, foodname, workingDate);
                                    }else
                                    {
                                        Toast.makeText(dialog.getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).start();
                            food.remove(i);
                            foodLinearAdapter fada = (foodLinearAdapter) ln.getAdapter();
                            fada.notifyDataSetChanged();
                        }
                        if (which == 2) {
                            Toast.makeText(dialog.getContext(), "Edit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.create();
                AlertDialog options = builder.create();
                options.show();
                return true;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                resetData();
                readExercisesFirebase();
            }
        });

        dialog.show();
    }

    void activityDetail(){
        final Dialog dialog = new Dialog(HistoryData.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_activity_details);

        final ValueLineChart mSteps = dialog.findViewById(R.id.steps_timeline);
        final ValueLineChart mHeart = dialog.findViewById(R.id.heart_reate_timeline);

        final ValueLineSeries steps = new ValueLineSeries();
        final ValueLineSeries heart = new ValueLineSeries();
        steps.setColor(0xFF56B7F1);
        heart.setColor(0xFF0DFFC1);

        SortedSet<String> keys = new TreeSet<>(activityRecords.keySet());
        for (String key : keys) {
            Map<String, String> entry = (Map) activityRecords.get(key);

            if (entry.get("Steps") != null) {
                steps.addPoint(new ValueLinePoint(key, Integer.parseInt(entry.get("Steps"))));
            } else {
                steps.addPoint(new ValueLinePoint(key, 0));
            }
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

    void back(){
        Intent intent = new Intent(getApplicationContext(), bandManagement.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
