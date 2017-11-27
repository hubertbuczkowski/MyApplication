package com.example.h_buc.activitytracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextView user;
    TextView pass;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

       // dbConnect var1 = new dbConnect();
        //var1.ConnectionToDB();
        user = findViewById(R.id.UsernameText);
        pass = findViewById(R.id.PassText);

        Button logBtn = (Button) findViewById(R.id.LoginBtn);
        Button regBtn = (Button) findViewById(R.id.RegBtn);

        logBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                login();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                register();
            }
        });

    }

    private boolean checkUser(String username, String password){

        if(username.isEmpty())
        {
            user.setError("Email is required");
            user.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            user.setError("Email is required");
            user.requestFocus();
            return false;
        }

        if(password.isEmpty())
        {
            pass.setError("Email is required");
            pass.requestFocus();
            return false;
        }

        return true;
    }

    private void login()
    {
        String username = user.getText().toString().trim();
        String password = pass.getText().toString().trim();

        if(checkUser(username, password)) {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Login.this, bandManagement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                         Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void register()
    {
        String username = user.getText().toString().trim();
        String password = pass.getText().toString().trim();


        if(checkUser(username, password)) {
            mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                            // ...
                        }
                    });
        }

    }
}