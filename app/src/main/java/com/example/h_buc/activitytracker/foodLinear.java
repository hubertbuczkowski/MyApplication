package com.example.h_buc.activitytracker;

import android.widget.Button;

import java.text.DecimalFormat;

/**
 * Created by h_buc on 08/04/2018.
 */

//class for linear layout adapted
public class foodLinear {
    String foodName;
    String weight;
    String proteins;
    String carbs;
    String fat;
    String cals;

    public foodLinear(String foodName, String weight, String prot,
                             String carb, String fat, String cals){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);
        this.foodName = foodName;
        this.weight = df.format(Float.parseFloat(weight));
        this.proteins = df.format(Float.parseFloat(prot));
        this.carbs = df.format(Float.parseFloat(carb));
        this.fat = df.format(Float.parseFloat(fat));
        this.cals = df.format(Float.parseFloat(cals));
    }
}
