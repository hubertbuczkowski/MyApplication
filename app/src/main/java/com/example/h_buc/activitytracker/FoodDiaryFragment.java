package com.example.h_buc.activitytracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


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
                addFood("Weight");
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
