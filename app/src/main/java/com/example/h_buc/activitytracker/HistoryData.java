package com.example.h_buc.activitytracker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by h_buc on 11/04/2018.
 */

public class HistoryData
{

//    void calculateFood(Map<String, Object> prods, String key)
//    {
//        DecimalFormat df = new DecimalFormat();
//        df.setMaximumFractionDigits(1);
//        for(String product : prods.keySet())
//        {
//            Map<String, String> details = (Map) prods.get(product);
//            if(key.equals("Breakfast"))
//            {
//                float cummulator = 0;
//                cummulator = Float.parseFloat(bPro.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
//                bPro.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(bCal.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
//                bCal.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(bFat.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
//                bFat.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(bCarb.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
//                bCarb.setText(df.format(cummulator));
//            }
//            if(key.equals("Lunch"))
//            {
//                float cummulator = 0;
//                cummulator = Float.parseFloat(lPro.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
//                lPro.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(lCal.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
//                lCal.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(lFat.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
//                lFat.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(lCarb.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
//                lCarb.setText(df.format(cummulator));
//            }
//            if(key.equals("Dinner"))
//            {
//                float cummulator = 0;
//                cummulator = Float.parseFloat(dPro.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
//                dPro.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(dCal.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
//                dCal.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(dFat.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
//                dFat.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(dCarb.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
//                dCarb.setText(df.format(cummulator));
//
//            }
//            if(key.equals("Supper"))
//            {
//                float cummulator = 0;
//                cummulator = Float.parseFloat(sPro.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
//                sPro.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(sCal.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
//                sCal.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(sFat.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
//                sFat.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(sCarb.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
//                sCarb.setText(df.format(cummulator));
//            }
//            if(key.equals("Snack"))
//            {
//                float cummulator = 0;
//                cummulator = Float.parseFloat(snPro.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Protein"));
//                snPro.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(snCal.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Calories"));
//                snCal.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(snFat.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Fat"));
//                snFat.setText(df.format(cummulator));
//                cummulator = 0;
//                cummulator = Float.parseFloat(snCarb.getText().toString());
//                cummulator = cummulator + Float.parseFloat(details.get("Carb"));
//                snCarb.setText(df.format(cummulator));
//            }
//
//        }
//    }
//
//    void sumFood(){
//        DecimalFormat df = new DecimalFormat();
//        df.setMaximumFractionDigits(1);
//        float prot = 0;
//        float carb = 0;
//        float fat = 0;
//        float cal = 0;
//
//        prot = prot + Float.parseFloat(bPro.getText().toString());
//        prot = prot + Float.parseFloat(lPro.getText().toString());
//        prot = prot + Float.parseFloat(dPro.getText().toString());
//        prot = prot + Float.parseFloat(sPro.getText().toString());
//        prot = prot + Float.parseFloat(snPro.getText().toString());
//        tPro.setText(df.format(prot));
//        carb = carb + Float.parseFloat(bCarb.getText().toString());
//        carb = carb + Float.parseFloat(lCarb.getText().toString());
//        carb = carb + Float.parseFloat(dCarb.getText().toString());
//        carb = carb + Float.parseFloat(sCarb.getText().toString());
//        carb = carb + Float.parseFloat(snCarb.getText().toString());
//        tCarb.setText(df.format(carb));
//        fat = fat + Float.parseFloat(bFat.getText().toString());
//        fat = fat + Float.parseFloat(lFat.getText().toString());
//        fat = fat + Float.parseFloat(dFat.getText().toString());
//        fat = fat + Float.parseFloat(sFat.getText().toString());
//        fat = fat + Float.parseFloat(snFat.getText().toString());
//        tFat.setText(df.format(fat));
//        cal = cal + Float.parseFloat(bCal.getText().toString());
//        cal = cal + Float.parseFloat(lCal.getText().toString());
//        cal = cal + Float.parseFloat(dCal.getText().toString());
//        cal = cal + Float.parseFloat(sCal.getText().toString());
//        cal = cal + Float.parseFloat(snCal.getText().toString());
//        tCal.setText(String.valueOf((int) cal));
//    }
//
//    void updateFood(Map<String, Object> entry)
//    {
//        foodDetails = entry;
//        for(String key : entry.keySet())
//        {
//            calculateFood((Map) entry.get(key), key);
//        }
//        sumFood();
//    }
//
//    void updateFood(Map<String, Object> entry)
//    {
//        foodDetails = entry;
//        for(String key : entry.keySet())
//        {
//            calculateFood((Map) entry.get(key), key);
//        }
//        sumFood();
//    }
//
//    void readExercisesFirebase (DatabaseReference database)
//    {
//        database.child("Records").child(new SimpleDateFormat("ddMMyyyy").format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String,Object> res = (Map<String, Object>) dataSnapshot.getValue();
//                if(dataSnapshot.exists()) {
//                    SortedSet<String> keys = new TreeSet<>(res.keySet());
//                    int previousSteps = 0;
//                    String previousTime = "00:00";
//                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//                    Date date1 = new Date();
//                    Date date2 = new Date();
//                    int steps = 0;
//                    int hr = 0;
//                    double doubleExcals = 0;
//                    activityRecords.clear();
//
//                    for (String key : keys) {
//                        if(!key.equals("Food") && !key.equals("Weight"))
//                        {
//                            Map<String, String> entry = (Map) res.get(key);
//                            activityRecords.put(key, entry);
//
//                            try {
//                                date1 = format.parse(previousTime);
//                                date2 = format.parse(key);
//                                previousTime = key;
//                            } catch (Exception e) {}
//
//                            long diff = date2.getTime() - date1.getTime();
//                            diff = diff / 60000;
//
//                            if(diff > 10)
//                            {
//                                diff = 10;
//                            }
//
//                            steps = Integer.parseInt(entry.get("Steps"));
//                            if (entry.get("Heart Rate") != null) {
//                                hr = Integer.parseInt(entry.get("Heart Rate"));
//                            } else {
//                                hr = 0;
//                            }
//
//                            if (hr > 80 && steps > previousSteps) {
//                                doubleExcals = (doubleExcals + ((calculateHR(diff, hr) + calculateSteps(steps - previousSteps)) / 2));
//                                previousSteps = steps;
//                            } else {
//                                if (hr > 80 || steps > previousSteps) {
//                                    if (hr > 80) {
//                                        doubleExcals = (doubleExcals + calculateHR(diff, hr));
//                                    } else {
//                                        doubleExcals = (doubleExcals + calculateSteps(steps - previousSteps));
//                                        previousSteps = steps;
//                                    }
//                                }
//                            }
//                        }
//                        else
//                        {
//                            if(!key.equals("Weight")) {
//                                updateFood((Map) res.get(key));
//                            }
//                        }
//                    }
//                    excals = (int) doubleExcals;
//                    updateCals();
//                } else
//                {
//                    excals = 0;
//                    updateCals();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
