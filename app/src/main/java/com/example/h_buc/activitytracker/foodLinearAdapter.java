package com.example.h_buc.activitytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by h_buc on 08/04/2018.
 */

public class foodLinearAdapter extends ArrayAdapter<foodLinear> {


    public foodLinearAdapter(Context context, ArrayList<foodLinear> food) {
        super(context, 0, food);
    }

    //linear view adapter which is responsible for updating data in meal detail dialog
    //on main screen and history acivity
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        foodLinear food = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_linear_adapter, parent, false);
        }
        // Lookup view for data population
        TextView protein = convertView.findViewById(R.id.protein);
        TextView carbs = convertView.findViewById(R.id.carbs);
        TextView fat = convertView.findViewById(R.id.fat);
        TextView weight = convertView.findViewById(R.id.weight);
        TextView calories = convertView.findViewById(R.id.calories);
        TextView name = convertView.findViewById(R.id.prod);

        protein.setText(food.proteins);
        carbs.setText(food.carbs);
        fat.setText(food.fat);
        weight.setText(food.weight);
        calories.setText(food.cals);
        name.setText(food.foodName);
        return convertView;
    }

}
