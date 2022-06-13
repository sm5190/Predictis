package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BpMonitor extends AppCompatActivity {
    TextView sisValue;
    TextView disValue;
    TextView pulseValue;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_monitor);
        toolbar = findViewById(R.id.include);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sisValue = findViewById(R.id.sisValue);
        disValue = findViewById(R.id.diaValue);
        pulseValue = findViewById(R.id.bpValue);
        firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query lastQuery = reference.child("Blood_Pressure").child(userID).orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("TAG",dataSnapshot.getValue().toString());
                    //Toast.makeText(BpMonitor.this, dataSnapshot.child("dia").getValue().toString(), Toast.LENGTH_SHORT).show();
                    String systolicPressure = dataSnapshot.child("0").getValue().toString();
                    String diastolicPressure = dataSnapshot.child("1").getValue().toString();
                    String pulse = dataSnapshot.child("2").getValue().toString();
                    sisValue.setText(systolicPressure);
                    disValue.setText(diastolicPressure);
                    pulseValue.setText(pulse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}