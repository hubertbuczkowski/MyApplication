package com.example.h_buc.activitytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.example.h_buc.activitytracker.Helpers.CheckConnection;
import com.example.h_buc.activitytracker.Helpers.FirebaseManagement;
import com.example.h_buc.activitytracker.Helpers.internalDatabaseManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by h_buc on 13/11/2017.
 */

//Main class which is responsible for all display and calculations

public class bandManagement extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageButton usrBtn, logout, history;
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

    BottomSheetDialogFragment bottomSheetDialogFragment;

    int excals;
    ArrayList<Map<String, String>> foodDetails;
    Map<String, Map<String, String>> activityRecords = new HashMap<>();

    //Start all listeners and is invoked once when activity is started
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

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                history();
            }
        });

        Intent i = new Intent(this, BackgroundService.class);

        getApplicationContext().startService(i);
    }

    //invoke reading database data and updating screen data
    protected void onResume(){
        super.onResume();
        resetData();
        if(checkSharedPreference())
        {
            readExerciseDatabase();
            readFoodDatabase();
        }
        else{
            userPref();
        }
    }

    //clear all data on the screen
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

    //initialise all screen elements
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
        logout = (ImageButton) findViewById(R.id.historyBack);
        addBtn = findViewById(R.id.addButton);
        ln1 = findViewById(R.id.linearBreakfast);
        ln2 = findViewById(R.id.linearLunch);
        ln3 = findViewById(R.id.linearDinner);
        ln4 = findViewById(R.id.linearSupper);
        ln5 = findViewById(R.id.linearSnack);
        history = findViewById(R.id.userHistory);

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

    //calculate HR calories
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

    //calculate steps calories
    double calculateSteps(int steps){
        return steps  * 0.044;
    }

    //update food data on the screen
    //sums all micro nutrients for each specific meal type
    void calculateFood(Map<String, String> details, String key)
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        float cummulator = 0;

        switch(key)
        {
            case "Breakfast":
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
                break;
            case "Lunch":
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
                break;
            case "Dinner":
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
                break;
            case "Supper":
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
                break;
            case "Snack":
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
                break;
        }
    }

    //Sum all data in total amount on the screen
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
        String random = bCal.getText().toString();
        cal = cal + Float.parseFloat(bCal.getText().toString().replace(",", ""));
        cal = cal + Float.parseFloat(lCal.getText().toString());
        cal = cal + Float.parseFloat(dCal.getText().toString());
        cal = cal + Float.parseFloat(sCal.getText().toString());
        cal = cal + Float.parseFloat(snCal.getText().toString());
        tCal.setText(String.valueOf((int) cal));
    }

    //read activity data from the database
    void readExerciseDatabase(){
        internalDatabaseManager db = new internalDatabaseManager(getApplicationContext());

        ArrayList<Map<String, String>> records = db.readRecords(new SimpleDateFormat("ddMMyyyy").format(new Date()));
        String previousTime = "00:00";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = new Date();
        Date date2 = new Date();
        int previousSteps = 0;
        int steps = 0;
        int hr = 0;
        double doubleExcals = 0;
        activityRecords.clear();

        for(Map<String, String> entry : records)
        {
            activityRecords.put(entry.get("Time"), entry);
            try {
                date1 = format.parse(previousTime);
                date2 = format.parse(entry.get("Time"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long diff = date2.getTime() - date1.getTime();
            diff = diff / 60000;

            if(diff > 10)
            {
                diff = 10;
            }

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
        excals = (int) doubleExcals;
    }

    //read all consumed food data from database
    void readFoodDatabase(){
        internalDatabaseManager db = new internalDatabaseManager(getApplicationContext());
        resetData();
        foodDetails = db.readFood(new SimpleDateFormat("ddMMyyyy").format(new Date()));

        for(Map<String, String> entry : foodDetails)
        {
            calculateFood(entry, entry.get("Meal"));
        }
        sumFood();
        updateCals();
    }

    //read activity details and put them in graphs
    //create dialog with graphs
    void activityDetail(){
        final Dialog dialog = new Dialog(bandManagement.this);
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

    //display all details of clicked meal type
    //also is responsible for opening meal editor, deleting meal and adding new meal
    void foodDetail(final String meal){
        final Dialog dialog = new Dialog(bandManagement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_food_details);
        final ListView ln = dialog.findViewById(R.id.foodListView);
        Button add = dialog.findViewById(R.id.foodDialogAddButton);
        TextView tx = dialog.findViewById(R.id.foodDialogType);
        tx.setText(meal);

        final ArrayList<foodLinear> food = new ArrayList<foodLinear>();
        foodLinearAdapter adapter;

        if(foodDetails != null)
        {
            for(Map<String, String> product : foodDetails)
            {
                if(product.get("Meal").equals(meal)) {
                    foodLinear fl = new foodLinear(product.get("Name"),
                            product.get("Weight"), product.get("Protein"), product.get("Carb"), product.get("Fat"), product.get("Calories"));
                    food.add(fl);
                }
            }
            adapter = new foodLinearAdapter(this, food);
            ln.setAdapter(adapter);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFood(meal);
            }
        });

        ln.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());
                builder.setTitle("What to do");
                builder.setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int which) {
                        if (which == 1) {
                            final internalDatabaseManager db = new internalDatabaseManager(getApplicationContext());
                            final String foodname = food.get(i).foodName;
                            new Thread(new Runnable() {
                                public void run() {
                                    db.deleteMeal(meal, foodname, new SimpleDateFormat("ddMMyyyy").format(new Date()));
                                    if(CheckConnection.InternetConnection()) {
                                        FirebaseManagement.deleteFood(meal, foodname, new SimpleDateFormat("ddMMyyyy").format(new Date()));
                                    }
                                }
                            }).start();
                            food.remove(i);
                            foodLinearAdapter fada = (foodLinearAdapter) ln.getAdapter();
                            fada.notifyDataSetChanged();
                        }
                        if (which == 0) {
                            manualDialog(food.get(i), dialog.getContext(), meal);
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
                readFoodDatabase();
            }
        });

        dialog.show();
    }

    //opens dialog where user can manually edit food details
    private void manualDialog(foodLinear meal, Context ctx, final String mealType) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manual_food_editor);

        final EditText protein = dialog.findViewById(R.id.editProtein);
        final EditText carbs = dialog.findViewById(R.id.editCarbs);
        final EditText fat = dialog.findViewById(R.id.editFat);
        final EditText calories = dialog.findViewById(R.id.editCalories);
        final EditText weight = dialog.findViewById(R.id.editWeight);
        final EditText name = dialog.findViewById(R.id.editName);
        final Button cancel = dialog.findViewById(R.id.manualCancel);
        final Button add = dialog.findViewById(R.id.manualAdd);

        if(meal.foodName != null)
        {
            name.setText(meal.foodName);
        }
        if(meal.weight != null)
        {
            weight.setText(meal.weight);
        }
        if(meal.proteins != null)
        {
            protein.setText(meal.proteins);
        }
        if(meal.carbs != null)
        {
            carbs.setText(meal.carbs);
        }
        if(meal.fat != null)
        {
            fat.setText(meal.fat);
        }
        if(meal.cals != null)
        {
            calories.setText(meal.cals);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err = 0;
                if(protein.getText().toString().isEmpty())
                {
                    err=1;
                    protein.setError("This field cannot be empty");
                }
                if(carbs.getText().toString().isEmpty())
                {
                    err=1;
                    carbs.setError("This field cannot be empty");
                }
                if(fat.getText().toString().isEmpty())
                {
                    err=1;
                    fat.setError("This field cannot be empty");
                }
                if(calories.getText().toString().isEmpty())
                {
                    err=1;
                    calories.setError("This field cannot be empty");
                }

                if(weight.getText().toString().isEmpty())
                {
                    err=1;
                    weight.setError("This field cannot be empty");
                }
                if(name.getText().toString().isEmpty())
                {
                    err=1;
                    name.setError("This field cannot be empty");
                }
                if(err == 0)
                {
                    int weightInt = Integer.parseInt(weight.getText().toString());
                    Map<String, String> dialogMap = new HashMap<>();
                    dialogMap.put("Date", new SimpleDateFormat("ddMMyyyy").format(new Date()));
                    dialogMap.put("Protein", String.valueOf((Float.parseFloat(protein.getText().toString())/100)*weightInt));
                    dialogMap.put("Carb", String.valueOf((Float.parseFloat(carbs.getText().toString())/100)*weightInt));
                    dialogMap.put("Fat", String.valueOf((Float.parseFloat(fat.getText().toString())/100)*weightInt));
                    dialogMap.put("Calories", String.valueOf((Float.parseFloat(calories.getText().toString())/100)*weightInt));
                    dialogMap.put("Weight", String.valueOf(Integer.parseInt(weight.getText().toString())));
                    dialogMap.put("Name", name.getText().toString());
                    dialogMap.put("Food Id", "01");
                    dialogMap.put("Meal", mealType);
                    updateMeal(dialogMap);
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //update edited details in databases
    void updateMeal(final Map<String, String> dialogMap){
        new Thread(new Runnable() {
            public void run() {
                internalDatabaseManager db = new internalDatabaseManager(getApplicationContext());
                if(CheckConnection.InternetConnection()) {
                    FirebaseManagement.updateFood(dialogMap.get("Name"), dialogMap.get("Food Id"), dialogMap.get("Weight"), dialogMap.get("Protein"),
                            dialogMap.get("Carb"), dialogMap.get("Fat"), dialogMap.get("Calories"), dialogMap.get("Meal"), dialogMap.get("Date"));
                    dialogMap.put("Sync", "true");
                    db.updateMeal(dialogMap);
                }
                else
                {
                    dialogMap.put("Sync", "false");
                    db.updateMeal(dialogMap);
                }
            }
        }).start();
    }

    //open user settings activity
    void userPref()
    {
        Intent intent = new Intent(bandManagement.this, userPref.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //open date picker which open activity with past selected data
    void history(){
        final Dialog dialog = new Dialog(bandManagement.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_picker);

        Button check = dialog.findViewById(R.id.historyConf);
        final DatePicker dt = dialog.findViewById(R.id.datePicker);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String day = String.valueOf(dt.getDayOfMonth());
                String month = String.valueOf(dt.getMonth()+1);
                String year = String.valueOf(dt.getYear());
                if(day.length() == 1)
                {
                    day = "0"+day;
                }

                if(month.length() == 1)
                {
                    month = "0"+month;
                }
                Intent intent = new Intent(bandManagement.this, HistoryData.class);
                intent.putExtra("Date", day+month+year);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    //logs user out
    void Logout(){
        SaveSharedPreference.clear(getApplicationContext());
        Intent intent = new Intent(bandManagement.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //opens activity for adding food
    void addFood(String meal){
        Intent intent = new Intent(getApplicationContext(), searchFood.class);
        intent.putExtra("Meal Type", meal);
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

    //check if user has all data provided in user settings
    private boolean checkSharedPreference(){
        if(SaveSharedPreference.getPrefGender(getApplicationContext()).isEmpty()){return false;}
        if(SaveSharedPreference.getPrefWeight(getApplicationContext()).isEmpty()){return false;}
        if(SaveSharedPreference.getPrefAge(getApplicationContext()).isEmpty()){return false;}
        if(SaveSharedPreference.getPrefHeight(getApplicationContext()).isEmpty()){return false;}
        return true;
    }

    //update equation caloric details and graph
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
                    bodycals = bodycals - 500;
                    break;
                case 2:
                    bodycals = bodycals + 500;
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

}
