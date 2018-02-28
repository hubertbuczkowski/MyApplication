package com.example.h_buc.activitytracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by h_buc on 22/01/2018.
 */

public class userPref extends AppCompatActivity {

    SeekBar sk;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView tx1;
    EditText fname, sname, email, height, weight;
    ImageButton backBtn;
    FirebaseUser currentUser;

    @SuppressLint("WrongViewCast")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initialiseFields();

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekChange(i, tx1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                save();
                back();
            }
        });
    }

    void initialiseFields(){
        backBtn = findViewById(R.id.backArrow);
        sk = findViewById(R.id.seekBar);
        tx1 =  findViewById(R.id.goalBar);
        fname = findViewById(R.id.editText);
        sname = findViewById(R.id.editText2);
        email = findViewById(R.id.editText4);
        height = findViewById(R.id.editText3);
        weight = findViewById(R.id.editText5);

        currentUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

        database.child("First name").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname.setText(dataSnapshot.getValue(String.class));
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        database.child("Surname").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                sname.setText(dataSnapshot.getValue(String.class));
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        database.child("Height").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                height.setText(dataSnapshot.getValue(String.class));
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        database.child("Weight").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                weight.setText(dataSnapshot.getValue().toString());
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        database.child("Goal").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String st = dataSnapshot.getValue().toString();
                seekChange(Integer.parseInt(st), tx1);
                sk.setProgress(Integer.parseInt(st));
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

        email.setText(currentUser.getEmail());
    }

    void save()
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
        database.child("First name").setValue(fname.getText().toString());
        database.child("Surname").setValue(sname.getText().toString());
        database.child("Height").setValue(height.getText().toString());
        database.child("Weight").setValue(weight.getText().toString());
        database.child("Goal").setValue(sk.getProgress());
    }

    void back(){

        finish();
    }

    void seekChange(int i, TextView tx1){
        switch (i){
            case 0:
                tx1.setText("Lose Weight");
                break;
            case 1:
                tx1.setText("Improve lifestyle");
                break;
            case 2:
                tx1.setText("Build muscle");
                break;
        }
    }

}
