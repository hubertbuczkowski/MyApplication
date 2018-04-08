package com.example.h_buc.activitytracker;

import android.widget.Button;

/**
 * Created by h_buc on 08/04/2018.
 */

public class foodLinear {
    String foodName;
    String weight;
    String proteins;
    String carbs;
    String fat;
    String cals;
    Button delete;

    public foodLinear(String foodName, String weight, String prot,
                             String carb, String fat, String cals){
        this.foodName = foodName;
        this.weight = weight;
        this.proteins = prot;
        this.carbs = carb;
        this.fat = fat;
        this.cals = cals;
    }
}
