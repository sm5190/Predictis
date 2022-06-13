package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    private DrawerLayout drawer;
    CardView card1,card2,card3,card4;
    TextView name,risk,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.getMenu().getItem(6).setChecked(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DeviceConnection.class));
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RiskZone.class));
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EmergencyContact.class));
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BarChartActivity.class));
            }
        });

        name = (TextView) findViewById(R.id.dashboardName);
        risk = (TextView) findViewById(R.id.dashboardPrediction);
        contact = (TextView) findViewById(R.id.dashboardContact);
        String userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    name.setText("Name: "+document.getString("Username"));
                    contact.setText("Emergency Contact: 0"+document.getLong("EmergencyContact").toString());
                    String prediction = document.getString("Prediction");
                    String prdictionZone = null;
                    switch (prediction){
                        case "0":
                            prdictionZone = "Green";
                            break;
                        case "1":
                            prdictionZone = "Yellow";
                            break;
                        case "2":
                            prdictionZone = "Red";
                            break;
                        default:
                            prdictionZone = "no";
                            break;
                    }
                    if (prediction.equals("no")){
                        risk.setText("Last Risk Zone: No prediction history");
                    }else{
                        risk.setText("Last Risk Zone: "+prdictionZone);
                    }
                }
            }
        });
//        auth = FirebaseAuth.getInstance();
//        Button logout = findViewById(R.id.logoutBtn);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(),Home.class));
//                finish();
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_riskZone:
                startActivity(new Intent(getApplicationContext(),RiskZone.class));
                break;
            case R.id.nav_pulse:
                startActivity(new Intent(getApplicationContext(),PulseMonitor.class));
                break;
            case R.id.nav_blood_pressure:
                startActivity(new Intent(getApplicationContext(),BpMonitor.class));
                break;
            case R.id.nav_logout:
                auth.signOut();
                startActivity(new Intent(getApplicationContext(),Home.class));
                finish();
                break;
            case R.id.nav_consultation:
                startActivity(new Intent(getApplicationContext(),EmergencyContact.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(),UpdateProfile.class));
                break;
            case R.id.nav_history:
                startActivity(new Intent(getApplicationContext(),BarChartActivity.class));
                break;
            case R.id.nav_exerciseRoutine:
                startActivity(new Intent(getApplicationContext(),EcgMonitor.class));
                break;
            case R.id.nav_deviceConnection:
                startActivity(new Intent(getApplicationContext(),DeviceConnection.class));
                break;
            case R.id.nav_diet:
                startActivity(new Intent(getApplicationContext(),MedicalConsultation.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

}