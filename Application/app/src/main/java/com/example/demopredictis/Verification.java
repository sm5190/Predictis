package com.example.demopredictis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Verification extends AppCompatActivity {
    TextView verifyText1,verifyText2,verifyText3,verifyText4;
    Button verifyBtn,goLoginBtn,logoutBtn;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        verifyText1 = findViewById(R.id.verifyText1);
        verifyText2 = findViewById(R.id.verifyText2);
        verifyText3 = findViewById(R.id.verifyText3);
        verifyText4 = findViewById(R.id.verifyText4);
        verifyBtn = findViewById(R.id.verifyBtn);
        goLoginBtn = findViewById(R.id.goLoginBtn);

        auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();
        DatabaseReference conn = firebaseDatabase.getInstance().getReference().child("Thalach").child(userID).child("Value").child("-AThalach");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setValue("100");
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        verifyText1.setVisibility(View.INVISIBLE);
                        verifyText2.setVisibility(View.INVISIBLE);
                        verifyText3.setVisibility(View.INVISIBLE);
                        verifyBtn.setVisibility(View.INVISIBLE);
                        verifyText4.setVisibility(View.VISIBLE);
                        goLoginBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
}