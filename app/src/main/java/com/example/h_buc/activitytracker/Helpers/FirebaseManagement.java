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

public class FirebaseManagement {



    void readAllRecords(){
    }

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
    }

}
