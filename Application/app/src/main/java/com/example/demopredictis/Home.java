package com.example.demopredictis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser()!=null && fAuth.getCurrentUser().isEmailVerified()){
            String userID = fAuth.getCurrentUser().getUid();
            DatabaseReference databaseReference = firebaseDatabase.getInstance().getReference().child("CurrentUser1").child("ID");
            databaseReference.setValue(userID);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    Button homeSignUp,homeLogin;
    TextView signupToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeLogin = findViewById(R.id.homeLogin);
        homeSignUp = findViewById(R.id.homeSignUp);
        homeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
        homeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(),Signup.class));
            }
        });
//        setContentView(R.layout.activity_signup);
//        signupToLogin = (TextView) this.findViewById(R.id.signupToLogin);
//        signupToLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),Login.class));
//            }
//        });
    }

}