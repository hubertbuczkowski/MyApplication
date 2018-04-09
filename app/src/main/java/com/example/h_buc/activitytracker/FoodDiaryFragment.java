package com.example.h_buc.activitytracker;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.example.h_buc.activitytracker.Helpers.FirebaseManagement;

import java.text.DecimalFormat;


public class FoodDiaryFragment extends BottomSheetDialogFragment {
    LinearLayout bf, ln, dn, su, sn, we;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        public void onStateChanged( View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        public void onSlide( View bottomSheet, float slideOffset) {
        }
    };

    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_item_list_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        bf = dialog.findViewById(R.id.breakfast);
        ln = dialog.findViewById(R.id.lunch);
        dn = dialog.findViewById(R.id.dinner);
        su = dialog.findViewById(R.id.supper);
        sn = dialog.findViewById(R.id.snack);
        we = dialog.findViewById(R.id.weight);

        bf.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addFood("Breakfast");
            }
        });
        ln.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addFood("Lunch");
            }
        });
        dn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addFood("Dinner");
            }
        });
        su.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addFood("Supper");
            }
        });
        sn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addFood("Snack");
            }
        });
        we.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.weight_picker);

                String [] values = new String[1500];
                double startingValue = 0;
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);
                for(int i = 0; i<1500; i++)
                {
                    values[i] = df.format(0.1*i);
                    if(!values[i].contains("."))
                    {
                        values[i] = values[i] + ".0";
                    }
                }

                Button bt = dialog.findViewById(R.id.dataPickerUpdate);
                final NumberPicker nr = dialog.findViewById(R.id.numberPicker2);
                nr.setMinValue(0);
                nr.setMaxValue(values.length-1);
                String weight = SaveSharedPreference.getPrefWeight(getContext());
                float wt = Float.parseFloat(weight)*10;
                nr.setValue((int) wt);
                nr.setDisplayedValues(values);
                nr.setWrapSelectorWheel(true);

                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SaveSharedPreference.setPrefWeight(getContext(), String.valueOf(nr.getValue()/10.0));
                        FirebaseManagement.setWeight(String.valueOf(nr.getValue()/10.0));
                        dialog.dismiss();
                    }
                });



                dialog.show();

            }
        });


        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    void addFood(String meal){
        Intent intent = new Intent(getActivity(), searchFood.class);
        intent.putExtra("Meal Type", meal);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
