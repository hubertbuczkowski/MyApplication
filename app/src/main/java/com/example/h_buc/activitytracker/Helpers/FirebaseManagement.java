package com.example.h_buc.activitytracker.Helpers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by h_buc on 09/04/2018.
 */

//This class is responsible for modyfying data on firebase database

public class FirebaseManagement {

    public static void deleteFood(String meal, String prod, String date){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
        Query delete = database.child("Records").child(date).child("Food").child(meal).orderByChild("Name").equalTo(prod);

        delete.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setWeight(String weight){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

        database.child("Records").child(new SimpleDateFormat("ddMMyyyy").format(new Date())).child("Weight").setValue(weight);
        database.child("Weight").setValue(weight);
    }

    public static void addFood(final String name, final String id, final String weight, final String protein, final String carbs, final String fat, final String cals, String titleString){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        String date = new SimpleDateFormat("ddMMyyyy").format(new Date());
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child("Records").child(date).child("Food").child(titleString);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                while(dataSnapshot.child("prod" + count).exists())
                {
                    count++;
                }
                database.child("prod" + count).child("Name").setValue(name);
                database.child("prod" + count).child("Id").setValue(id);
                database.child("prod" + count).child("Weight").setValue(weight);
                database.child("prod" + count).child("Protein").setValue(protein);
                database.child("prod" + count).child("Carb").setValue(carbs);
                database.child("prod" + count).child("Fat").setValue(fat);
                database.child("prod" + count).child("Calories").setValue(cals);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void updateFood(final String name, final String id, final String weight, final String protein, final String carbs, final String fat, final String cals, String titleString, final String date){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        deleteFood(titleString, name, date);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child("Records").child(date).child("Food").child(titleString);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                while(dataSnapshot.child("prod" + count).exists())
                {
                    count++;
                }
                database.child("prod" + count).child("Name").setValue(name);
                database.child("prod" + count).child("Id").setValue(id);
                database.child("prod" + count).child("Weight").setValue(weight);
                database.child("prod" + count).child("Protein").setValue(protein);
                database.child("prod" + count).child("Carb").setValue(carbs);
                database.child("prod" + count).child("Fat").setValue(fat);
                database.child("prod" + count).child("Calories").setValue(cals);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void addMissingFood(final String date, final String name, final String id, final String weight, final String protein, final String carbs, final String fat, final String cals, String titleString){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid()).child("Records").child(date).child("Food").child(titleString);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                while(dataSnapshot.child("prod" + count).exists())
                {
                    count++;
                }
                database.child("prod" + count).child("Name").setValue(name);
                database.child("prod" + count).child("Id").setValue(id);
                database.child("prod" + count).child("Weight").setValue(weight);
                database.child("prod" + count).child("Protein").setValue(protein);
                database.child("prod" + count).child("Carb").setValue(carbs);
                database.child("prod" + count).child("Fat").setValue(fat);
                database.child("prod" + count).child("Calories").setValue(cals);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void addRecord(String date, String time, String hr, String steps){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

        database.child("Records").child(date).child(time).child("Steps").setValue(steps);
        database.child("Records").child(date).child(time).child("Heart Rate").setValue(hr);
    }

    public static void readAllRecords(String date){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser  = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
    }


}
